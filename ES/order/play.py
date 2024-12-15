import json
import pymysql
import subprocess
import os
import sys
import requests
from datetime import datetime
from time import sleep

# MySQL 연결 설정
DB_CONFIG = {
    "host": "localhost",
    "user": "capstone",
    "password": "capstone0000",
    "database": "order_system"
}

# 데이터베이스 연결
def connect_db():
    try:
        connection = pymysql.connect(**DB_CONFIG)
        print("[INFO] 데이터베이스에 연결 성공!")
        return connection
    except Exception as e:
        print("[ERROR] 데이터베이스 연결 실패:", e)
        return None

# 주문 로그 기록
def log_order_to_db(order_uuid, order_time, items, status):
    connection = connect_db()
    if not connection:
        return False
    
    try:
        cursor = connection.cursor()
        query = "INSERT INTO order_logs (order_uuid, order_time, items, status) VALUES (%s, %s, %s, %s)"
        cursor.execute(query, (order_uuid, order_time, json.dumps(items, ensure_ascii=False), status))
        connection.commit()
        print(f"[INFO] 주문 로그 저장 완료 (UUID: {order_uuid})")
        return True
    except Exception as e:
        print("[ERROR] 주문 로그 저장 중 오류 발생:", e)
        return False
    finally:
        connection.close()

# 아이템에 해당하는 motor_id와 최대 수량을 가져오기
def get_item_motor_id_and_max_quantity(product_code):
    connection = connect_db()
    if not connection:
        return None, None

    try:
        cursor = connection.cursor(pymysql.cursors.DictCursor)
        cursor.execute("SELECT motor_id, stock_quantity FROM items WHERE product_code = %s", (product_code,))
        result = cursor.fetchone()
        if result:
            return result['motor_id'], result['stock_quantity']
        return None, None
    except Exception as e:
        print("[ERROR] 아이템 조회 중 오류 발생:", e)
        return None, None
    finally:
        connection.close()

# 재고 수량을 차감하는 함수
def update_stock_quantity(product_code, quantity):
    connection = connect_db()
    if not connection:
        return False

    try:
        cursor = connection.cursor()
        cursor.execute(
            "UPDATE items SET stock_quantity = GREATEST(stock_quantity - %s, 0) WHERE product_code = %s", 
            (quantity, product_code)
        )
        connection.commit()
        print(f"[INFO] '{product_code}'의 재고 수량 {quantity} 차감 완료")
        return True
    except Exception as e:
        print("[ERROR] 재고 차감 중 오류 발생:", e)
        return False
    finally:
        connection.close()

# 내부 파일 실행
def execute_file(file_path, args=None):
    if os.path.isfile(file_path):
        try:
            command = ["python3", file_path]
            if args:
                command.extend(args)  # 인자들을 command 리스트에 추가
            subprocess.run(command, check=True)
            print(f"[INFO] {file_path} 실행 완료!")
            return True  # 성공적으로 실행된 경우 True 반환
        except subprocess.CalledProcessError as e:
            print(f"[ERROR] {file_path} 실행 중 오류 발생: {e}")
            return False  # 실행 실패한 경우 False 반환
    else:
        print(f"[ERROR] 파일 {file_path}이(가) 존재하지 않습니다.")
        return False  # 파일이 존재하지 않는 경우 False 반환

# 입력 받은 데이터 정규화 및 유효성 검증
def normalize_and_validate_json(parsed_data):
    # 기존 키와 매핑
    key_map = {
        "id": "order_uuid",
        "orderAt": "order_time",
        "orderItems": "items"
    }

    # 키 이름 정규화
    normalized_data = {key_map.get(k, k): v for k, v in parsed_data.items()}

    # items 내부의 구조도 정규화
    normalized_data["items"] = [
        {"item": item["productCode"], "quantity": item["quantity"]}
        for item in parsed_data.get("orderItems", [])
    ]

    # 유효성 검증
    required_keys = {"items", "order_uuid", "order_time"}
    if required_keys.issubset(normalized_data.keys()):
        return normalized_data
    return None

# 날짜 형식 변환 함수
def convert_order_time(order_time_str):
    try:
        order_time = datetime.fromisoformat(order_time_str.replace("Z", "+00:00"))  # UTC 표기를 시간대 오프셋으로 변환
        return order_time.strftime("%Y-%m-%d %H:%M:%S")
    except Exception as e:
        print("[ERROR] 날짜 형식 변환 중 오류 발생:", e)
        return None

# 비어 있는 보관함 찾기(앱 주문시)
def get_available_locker():
    connection = connect_db()
    if not connection:
        return None
    
    try:
        cursor = connection.cursor(pymysql.cursors.DictCursor)
        # locker_number가 1~4 사이인 경우만 검색
        cursor.execute("""
            SELECT locker_number 
            FROM lockers 
            WHERE is_available = 1 AND locker_number BETWEEN 1 AND 4 
            ORDER BY locker_number 
            LIMIT 1
        """)
        locker = cursor.fetchone()
        return locker['locker_number'] if locker else None
    except Exception as e:
        print("[ERROR] 보관함 조회 중 오류 발생:", e)
        return None
    finally:
        connection.close()


# 보관함 상태 업데이트 (1: 비어있음, 0: 사용 중)
def update_locker_status(locker_number, status):
    connection = connect_db()
    if not connection:
        return

    try:
        cursor = connection.cursor()
        cursor.execute("UPDATE lockers SET is_available = %s WHERE locker_number = %s", (status, locker_number))
        connection.commit()
        print(f"[INFO] 보관함 {locker_number} 상태 업데이트 완료! (is_available={status})")
    except Exception as e:
        print("[ERROR] 보관함 상태 업데이트 중 오류 발생:", e)
    finally:
        connection.close()

# 보관함 로그 기록 (0: 보관함 할당, 1: 보관 중, 2: 수령 완료)
def log_locker_assignment(order_uuid, locker_number):
    connection = connect_db()
    if not connection:
        return False
    
    try:
        cursor = connection.cursor()
        # locker_number가 5일 경우 상태를 2로 설정, 그렇지 않으면 1로 설정
        status = 2 if locker_number == 5 else 1 
        cursor.execute("""
            INSERT INTO locker_logs (order_uuid, locker_number, status)
            VALUES (%s, %s, %s)
        """, (order_uuid, locker_number, status))  # 상태 업데이트
        connection.commit()
        print(f"[INFO] 보관함 할당 기록 저장 (UUID: {order_uuid}, Locker Number: {locker_number}), Status: {status})")
        return True
    except Exception as e:
        print("[ERROR] 보관함 할당 기록 저장 중 오류 발생:", e)
        return False
    finally:
        connection.close()

# 주문 데이터 처리
def process_order(parsed_data):
    try:
        # UUID 및 시간 처리
        order_uuid = parsed_data['order_uuid']
        order_time = convert_order_time(parsed_data['order_time'])
        if not order_time:
            print("잘못된 주문 시간 형식")
            return
        
        # 주문 로그 기록
        items = parsed_data['items']
        if not log_order_to_db(order_uuid, order_time, items, '대기 중'):
            print("주문 로그 저장 실패")
            return

        # userId가 72일 경우 보관함 5번으로 고정
        if parsed_data.get('userId') == 72:
            locker_number = 5
            print("[INFO] userId가 72이므로 5번 보관함으로 고정")
        else:
            # 비어 있는 보관함 확인 및 할당
            locker_number = get_available_locker()
            
        if locker_number:
            # locker_number에 따라 동적으로 파일 실행
            if 1 <= locker_number <= 5:  # locker_number가 1부터 5 사이일 경우 처리
                print(f"[INFO] {locker_number}번 보관함으로 이동")

                # locker_number가 5인 경우
                if locker_number == 5:
                    # 1_close.py부터 4_close.py 실행
                    for i in range(1, 5):
                        close_file = f"/home/capstone/order/moter/storage_in/{i}_close.py"
                        print(f"[INFO] 실행 중: {close_file}")
                        execute_file(close_file)
                        sleep(2)
                        print(f"2초 대기")
                else:
                    # 1_close.py부터 4_close.py 중 입력된 번호를 제외하고 실행
                    for i in range(1, 5):
                        if i != locker_number:  # 입력된 번호는 제외
                            close_file = f"/home/capstone/order/moter/storage_in/{i}_close.py"
                            print(f"[INFO] 실행 중: {close_file}")
                            execute_file(close_file)

                    # {n}_open.py 실행
                    open_file = f"/home/capstone/order/moter/storage_in/{locker_number}_open.py"
                    print(f"[INFO] 실행 중: {open_file}")
                    execute_file(open_file)

            else:
                print(f"[ERROR] 유효하지 않은 locker_number: {locker_number}")
            
            # userId가 72일 경우 5번 보관함은 상태 업데이트 하지 않음
            if locker_number != 5:
                # 보관함 상태를 '사용 중'으로 업데이트
                update_locker_status(locker_number, 0)

            # 보관함 할당 기록
            log_locker_assignment(order_uuid, locker_number)
                    
        else:
            print("[INFO] 사용 가능한 보관함이 없습니다.")

            # Flask 서버로 데이터를 전송
            payload = {
                "id": order_uuid,
                "result": False,
                "orderState": "PENDING",
                "orderItems": []  # 빈 리스트로 초기화
            }

            # 모든 items의 수량을 0으로 설정
            for item in items:
                payload["orderItems"].append({
                    "productCode": item.get("productCode", "unknown"),
                    "quantity": 0  # 수량 0으로 설정
                })

            try:
                response = requests.post(
                    "http://127.0.0.1:8081/receive_complete",
                    json=payload,
                    headers={
                        "Content-Type": "application/json"
                    }
                )
                response.raise_for_status()  # 요청 실패 시 예외 발생
                print("[INFO] Flask 서버로 데이터를 성공적으로 전송했습니다.")
            except Exception as e:
                print("[ERROR] Flask 서버로 데이터 전송 중 오류 발생:", e)

            return    

        # 각 아이템 처리
        for item in items:
            product_code = item['item']
            motor_id, max_quantity = get_item_motor_id_and_max_quantity(product_code)
            if not motor_id:
                print(f"'{product_code}'는 판매 중이 아닙니다.")
                continue

            if item['quantity'] > max_quantity:
                print(f"'{product_code}'의 주문 수량 초과")
                return

            # 모터 실행
            motor_script = f"/home/capstone/order/moter/print/step_m{motor_id}.py"
            for _ in range(item['quantity']):
                execute_file(motor_script)

            # 재고 차감
            if not update_stock_quantity(product_code, item['quantity']):
                print("재고 차감 실패")
                return

        # 완료 표시
        execute_file("/home/capstone/order/moter/play_check.py")

        # Flask 서버로 완료 데이터를 전송
        payload = {
            "id": order_uuid,
            "result": True,
            "orderState": "PENDING",  # 실패 상태로 설정
            "orderItems": []  # 빈 리스트로 초기화
        }

        for item in items:
            payload["orderItems"].append({
                "productCode": item.get("productCode", "unknown"),
                "quantity": item.get("quantity", "0")
            })

        try:
            response = requests.post(
                "http://127.0.0.1:8081/receive_complete",
                json=payload,
                headers={
                    "Content-Type": "application/json"
                }
            )
            response.raise_for_status()  # 요청 실패 시 예외 발생
            print("[INFO] Flask 서버로 데이터를 성공적으로 전송했습니다.")
        except Exception as e:
            print("[ERROR] Flask 서버로 데이터 전송 중 오류 발생:", e)

    except Exception as e:
        print("[ERROR] 처리 중 오류 발생:", e)

if __name__ == "__main__":
    order_data = None  # 기본값 설정

    if len(sys.argv) > 1:
        try:
            order_data = json.loads(sys.argv[1])  # 첫 번째 인자로 전달된 JSON 문자열을 파싱
            print(f"받은 주문 데이터: {order_data}")
        except json.JSONDecodeError as e:
            print(f"JSON 파싱 오류: {e}")
    else:
        print("데이터가 전달되지 않았습니다.")

    if order_data:
        normalized_data = normalize_and_validate_json(order_data)
        if normalized_data:
            process_order(normalized_data)
        else:
            print("유효하지 않은 JSON 데이터")
    else:
        print("주문 데이터가 유효하지 않거나 전달되지 않았습니다.")
