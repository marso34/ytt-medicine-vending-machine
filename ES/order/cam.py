import time
import cv2
import pymysql
import subprocess
import os
from pyzbar.pyzbar import decode
from picamera2 import Picamera2
import requests

# DB 연결 설정
DB_CONFIG = {
    "host": "localhost",
    "user": "capstone",
    "password": "capstone0000",
    "database": "order_system"
}

# 데이터베이스 연결 함수
def connect_db():
    try:
        connection = pymysql.connect(**DB_CONFIG)
        print("[INFO] 데이터베이스에 연결 성공!")
        return connection
    except Exception as e:
        print("[ERROR] 데이터베이스 연결 실패:", e)
        return None

# 내부 파일 실행 함수
def execute_file(file_path, locker_number=None):
    if os.path.isfile(file_path):
        try:
            # locker_number가 있는 경우 인자로 전달
            if locker_number is not None:
                subprocess.run(["python3", file_path, str(locker_number)], check=True)
            else:
                subprocess.run(["python3", file_path], check=True)
            print(f"[INFO] {file_path} 실행 완료!")
        except Exception as e:
            print(f"[ERROR] {file_path} 실행 중 오류 발생:", e)
    else:
        print(f"[ERROR] 파일 {file_path}이(가) 존재하지 않습니다.")

# 서버로 메시지 전송 (성공/실패)
def send_message_to_server(message, status="success"):
    server_url = "http://127.0.0.1:8081/api/message"  # app.py의 메시지 처리 엔드포인트
    payload = {"message": message, "status": status}
    try:
        response = requests.post(server_url, json=payload)
        if response.status_code == 200:
            print(f"[INFO] 메시지 전송 완료: {message}")
        else:
            print(f"[ERROR] 메시지 전송 실패: {response.status_code}")
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] 서버 연결 오류: {e}")

# QR 코드 검증 요청 함수
def send_qr_to_server(order_id):
    server_url = "http://127.0.0.1:8081/receive_order_id"  # QR 처리 엔드포인트
    payload = {"order_id": order_id}  # QR 코드 데이터를 orderId로 전송
    try:
        response = requests.post(server_url, json=payload)
        if response.status_code == 200:
            print(f"[INFO] QR 코드 전송 완료: {order_id}")
        else:
            print(f"[ERROR] QR 코드 전송 실패: {response.status_code}, {response.text}")
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] 서버 연결 오류: {e}")

# 30초 동안 QR 코드 감지 후 UUID 확인 및 파일 실행
def process_qr_code():
    picam2 = Picamera2()
    picam2.configure(picam2.create_preview_configuration(main={"size": (1920, 1080)}))
    picam2.start()

    # QR 코드 트리거 시 실행할 파일 경로
    QR_TRIGGER_FILE = "/home/capstone/order/specific_task.py"

    # 카메라 미리보기 화면을 띄우는 OpenCV 윈도우를 실행
    cv2.namedWindow("Camera Preview", cv2.WINDOW_NORMAL)

    start_time = time.time()

    try:
        while time.time() - start_time < 30:  # 30초 동안 반복
            # 카메라에서 이미지 캡처
            frame = picam2.capture_array()
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)  # OpenCV 호환 포맷으로 변환

            # QR 코드 인식
            qr_codes = decode(frame)

            for qr in qr_codes:
                qr_data = qr.data.decode('utf-8')
                print(f"[INFO] QR 코드 감지: {qr_data}")

                # 외부서버에 qr코드 검증
                send_qr_to_server(qr_data)

                # DB에서 '보관 중' 상태인 로그 찾기 (status = 1)
                connection = connect_db()
                if connection:
                    cursor = connection.cursor()
                    cursor.execute("""
                        SELECT order_uuid, locker_number FROM locker_logs
                        WHERE status = 1  # status 컬럼을 사용
                    """)
                    rows = cursor.fetchall()

                    matched = False  # QR 코드와 일치하는 UUID가 있는지 확인하는 플래그

                    # QR 코드 데이터가 DB의 order_uuid와 일치하면 작업 수행
                    for row in rows:
                        order_uuid = row[0]
                        locker_number = row[1]
                        if qr_data == order_uuid:
                            matched = True  # 일치하는 UUID가 있으면 True로 설정
                            print(f"[INFO] QR 코드와 주문 UUID 일치: {order_uuid}, 파일 실행 중...")

                            # 해당 파일 실행
                            execute_file(QR_TRIGGER_FILE, locker_number)

                            # locker_logs 테이블에서 상태를 '수령완료' (2)로 업데이트
                            cursor.execute("""
                                UPDATE locker_logs
                                SET status = 2, status_updated_at = NOW()
                                WHERE order_uuid = %s
                            """, (order_uuid,))
                            connection.commit()

                            # lockers 테이블에서 해당 보관함 번호를 '사용 가능' (is_available = 1)로 업데이트
                            cursor.execute("""
                                UPDATE lockers
                                SET is_available = 1
                                WHERE locker_number = %s
                            """, (locker_number,))
                            connection.commit()

                            print(f"[INFO] 보관함 상태 업데이트 완료 (UUID: {order_uuid}, Locker: {locker_number})")
                            break

                    if not matched:
                        send_message_to_server(f"QR 코드와 일치하는 UUID가 없습니다: {qr_data}", "error")
                        print(f"[INFO] QR 코드와 일치하는 UUID가 없습니다: {qr_data}")  # 일치하지 않으면 메시지 출력

                    cursor.close()  # 커서 닫기
                    connection.close()  # 연결 종료

                    # QR 코드 인식 후 즉시 종료
                    send_message_to_server(f"QR 코드와 주문 UUID 일치: {order_uuid} 작업 완료", "success")
                    print("[INFO] QR 코드 감지 후 작업 완료. 종료합니다.")
                    picam2.stop()
                    cv2.destroyAllWindows()
                    return  # 즉시 종료

                else:
                    print("[ERROR] DB 연결 실패")

            # 카메라 미리보기 화면 띄우기
            cv2.imshow("Camera Preview", frame)

            # 'q' 키를 눌러 종료
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

    except KeyboardInterrupt:
        print("[INFO] 사용자에 의해 종료되었습니다.")

    # 30초 후 카메라 종료
    picam2.stop()
    cv2.destroyAllWindows()

    send_message_to_server("30초 동안 QR 코드 인식 실패.", "error")

if __name__ == "__main__":
    process_qr_code()
