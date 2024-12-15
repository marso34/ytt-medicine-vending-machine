from flask import Flask, request, jsonify, render_template
from flask_cors import CORS
import pymysql
import websocket
import json
import threading
import logging
import subprocess
import requests
import os

# Flask 서버 초기화
app = Flask(__name__)
CORS(app)  # CORS 설정 추가

# 로깅 설정
logging.basicConfig(level=logging.INFO)

# MySQL 연결 설정
DB_CONFIG = {
    "host": "localhost",
    "user": "capstone",
    "password": "capstone0000",
    "database": "order_system"
}

# config.json 파일을 열어서 데이터 로드
with open('config.json', 'r') as f:
    config = json.load(f)

# config.json의 데이터 불러오기
True_jwt_token = config.get('True_jwt_token')
ws_url = config.get('ws_url')
True_url = config.get('True_url')

# stomp 서버 연결하기
class VendingMachineClient:
    def __init__(self):
        self.vending_machine_id = 2
        self.ws = None
        self.connected = False

    def connect(self):
        websocket.enableTrace(False)
        self.ws = websocket.WebSocketApp(
            "{ws_url}/ws/websocket",
            on_open=self.on_open,
            on_message=self.on_message,
            on_error=self.on_error,
            on_close=self.on_close,
            header=[
                "Accept-Version: 1.1,1.0",
                "Heart-Beat: 10000,10000"
            ]
        )
        threading.Thread(target=self.ws.run_forever, daemon=True).start()

    def on_open(self, ws):
        logging.info("WebSocket 연결 성공")
        self.send_connect_frame()

    def send_connect_frame(self):
        frame = "CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\0"
        self.ws.send(frame)
        logging.info("CONNECT 프레임 전송 완료")

    def subscribe_to_orders(self):
        frame = (
            f"SUBSCRIBE\ndestination:/topic/orders/vending-machine/{self.vending_machine_id}\n"
            f"id:sub-0\nack:auto\n\n\0"
        )
        self.ws.send(frame)
        logging.info(f"자판기 {self.vending_machine_id}의 주문 구독 시작")

    def on_message(self, ws, message):
        logging.info(f"수신 메시지: {message}")
        
        if "CONNECTED" in message:
            self.connected = True
            self.subscribe_to_orders()
        elif "MESSAGE" in message:
            body_start = message.find("{")
            body_end = message.rfind("}") + 1
            if body_start != -1 and body_end != -1:
                body = json.loads(message[body_start:body_end])
                logging.info(f"수신한 데이터: {body}")

                # 수신한 데이터의 형식이 맞는지 확인
                if 'id' in body and 'userId' in body and 'orderState' in body and 'orderItems' in body:
                    if 'userName' in body:
                        logging.info("userName이 포함되어 있어 play.py를 실행하지 않습니다.")
                        return  # userName이 포함되면 실행하지 않음
                    if 'code' in body:
                        logging.info("code가 포함되어 있어 play.py를 실행하지 않습니다.")
                        return  # code가 포함되면 실행하지 않음

                    try:
                        # 내부 파일(play.py) 실행 전에 데이터 전달
                        file_path = "/home/capstone/order/play.py"

                        # play.py를 실행할 때 JSON 데이터를 인자로 전달
                        execute_file(file_path, body)  # play.py 실행 시 데이터를 전달

                        logging.info(f"주문 데이터를 play.py에 전달 완료: {body}")
                    except Exception as e:
                        logging.error(f"내부 파일 실행 중 오류 발생: {e}")
                else:
                    logging.error("잘못된 데이터 형식: 필요한 필드가 누락되었습니다.")

    def on_error(self, ws, error):
        logging.error(f"WebSocket 에러 발생: {error}")

    def on_close(self, ws, close_status_code, close_msg):
        self.connected = False
        logging.info("WebSocket 연결 종료")

    def disconnect(self):
        if self.ws:
            self.ws.close()
            logging.info("WebSocket 연결 해제 완료")

# 데이터베이스에서 아이템 목록 가져오기
def get_items_from_db():
    connection = None
    try:
        logging.info("데이터베이스에 연결 시도 중...")
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()

        # SQL 쿼리 작성 및 실행
        sql = "SELECT item_name, stock_quantity FROM items"
        cursor.execute(sql)
        result = cursor.fetchall()

        # 결과를 딕셔너리 형태로 반환
        items = [{"name": row[0], "maxQuantity": row[1]} for row in result]
        
        # 가져온 아이템 목록을 로그로 출력
        logging.info("데이터베이스에서 가져온 아이템 목록:")
        logging.info(items)  # JSON처럼 보기 쉽게 출력
        
        return items
    except Exception as e:
        logging.error(f"데이터베이스 연결 중 오류 발생: {e}")
        return []
    finally:
        if connection:
            connection.close()
            logging.info("데이터베이스 연결 종료.")

# 약 이름을 productCode로 매핑
def get_product_code(name):

    connection = pymysql.connect(**DB_CONFIG)
    try:
        with connection.cursor() as cursor:
            query = "SELECT product_code FROM items WHERE item_name = %s"
            cursor.execute(query, (name,))
            result = cursor.fetchone()
            return result[0] if result else None
    finally:
        connection.close()

# 내부 파일 실행
def execute_file(file_path, data=None):
    if os.path.isfile(file_path):
        try:
            command = ["python3", file_path]
            if data:
                # JSON 데이터를 문자열로 변환하여 인자로 전달
                command.append(json.dumps(data))  # args에 데이터를 추가
            subprocess.run(command, check=True)
            print(f"[INFO] {file_path} 실행 완료!")
            return True  # 성공적으로 실행된 경우 True 반환
        except subprocess.CalledProcessError as e:
            print(f"[ERROR] {file_path} 실행 중 오류 발생: {e}")
            return False  # 실행 실패한 경우 False 반환
    else:
        print(f"[ERROR] 파일 {file_path}이(가) 존재하지 않습니다.")
        return False  # 파일이 존재하지 않는 경우 False 반환

# 클라이언트에서 아이템 목록을 요청하면 데이터베이스에서 가져와 반환
@app.route('/api/items', methods=['GET'])
def get_items():
    items = get_items_from_db()
    if items:
        return jsonify(items), 200
    else:
        return jsonify({"error": "아이템 목록을 가져오는 데 실패했습니다."}), 500

# 클라이언트에서 주문 데이터를 수신하고 처리
@app.route('/receive_order', methods=['POST'])
def receive_order():
    order_data = request.json
    logging.info(f"----------------------------------------------------------------------------")
    logging.info(f"HTML로부터 받은 주문 데이터: {order_data}")

    # 외부 서버로 전송할 데이터 생성
    try:
        # 리스트 데이터를 처리
        order_items = order_data  # 전달받은 리스트를 그대로 사용
        payload = {
            "userId": 72,  # 사용자 ID는 하드코딩
            "vendingMachineId": client.vending_machine_id,
            "orderItems": [
                {
                    "productCode": get_product_code(item.get("name", "")),  # 'name'을 'productCode'로 매핑
                    "quantity": item.get("quantity", 0)
                }
                for item in order_items
            ]
        }

        logging.info(f"외부 서버로 전송할 데이터: {payload}")

        # JWT 토큰 설정
        jwt_token = True_jwt_token

        # 외부 서버로 데이터 전송
        response = requests.post(
            "{True_url}/orders/create",
            json=payload,
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {jwt_token}"  # JWT 토큰 포함
            }
        )
        response.raise_for_status()  # 요청이 실패하면 예외 발생

        logging.info(f"외부 서버 응답: {response.status_code}, {response.text}")
        return jsonify({"status": "success", "message": "Order sent to external server"}), 200

    except Exception as e:
        logging.error(f"외부 서버로 데이터 전송 중 오류 발생: {e}")
        return jsonify({"status": "error", "message": f"Failed to send order: {str(e)}"}), 500

# 주문 데이터를 처리 완료 보고
@app.route('/receive_complete', methods=['POST'])
def receive_complete():
    order_data = request.json
    logging.info(f"자판기 받은 주문 데이터: {order_data}")

    try:
        # 데이터 검증
        order_id = order_data.get("id")
        result = order_data.get("result")
        order_state = order_data.get("orderState")
        order_items = order_data.get("orderItems", [])

        # 필수 필드 검증
        if not order_id or result is None or not order_state or not isinstance(order_items, list):
            raise ValueError("id, result, orderState 또는 orderItems 데이터가 유효하지 않습니다.")

        # JWT 토큰 설정
        jwt_token = True_jwt_token

        # 외부 서버로 데이터 전송
        response = requests.post(
            f"{True_url}/orders/store/{order_id}",  # order_id를 경로에 포함
            json=order_data,  # 받은 데이터를 그대로 전송
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {jwt_token}"  # JWT 토큰 포함
            }
        )
        response.raise_for_status()  # 요청이 실패하면 예외 발생

        logging.info(f"외부 서버 응답: {response.status_code}, {response.text}")
        return jsonify({"status": "success", "message": "Order sent to external server"}), 200

    except Exception as e:
        logging.error(f"외부 서버로 데이터 전송 중 오류 발생: {e}")
        return jsonify({"status": "error", "message": f"Failed to send order: {str(e)}"}), 500

# 클라이언트에서 QR코드를 수신하고 처리
@app.route('/receive_order_id', methods=['POST'])
def receive_order_id():
    order_id = request.json.get("order_id")
    logging.info(f"입력받은 qr코드 : {order_id}")

    # 외부 서버로 전송할 데이터 생성
    try:
        logging.info(f"외부 서버로 전송할 데이터: {order_id}")

        # JWT 토큰 설정
        jwt_token = True_jwt_token

        # 외부 서버 URL에 order_id 포함
        url = f"{True_url}/orders/complete/{order_id}"

        # 외부 서버로 데이터 전송
        response = requests.post(
            url,
            # json=order_id,
            headers={
                "Content-Type": "application/json",
                "Authorization": f"Bearer {jwt_token}"  # JWT 토큰 포함
            }
        )
        response.raise_for_status()  # 요청이 실패하면 예외 발생

        logging.info(f"외부 서버 응답: {response.status_code}, {response.text}")
        return jsonify({"status": "success", "message": "Order sent to external server"}), 200

    except Exception as e:
        logging.error(f"외부 서버로 데이터 전송 중 오류 발생: {e}")
        return jsonify({"status": "error", "message": f"Failed to send order: {str(e)}"}), 500

# 클라이언트에서 찾기 요청시 qr코드 파일 실행
@app.route('/run_cam', methods=['POST'])
def run_cam():
    file_path = "/home/capstone/order/cam.py"
    try:
        # 파일 실행 시도
        execute_file(file_path)
        # 파일 실행 성공 시 응답
        return jsonify({"status": "success", "message": f"{file_path} 실행 완료!"}), 200
    except Exception as e:
        # 실행 중 오류 발생 시 응답
        return jsonify({"status": "error", "message": f"실행 중 오류 발생: {str(e)}"}), 500

# 오류 메시지 처리 엔드포인트
@app.route('/api/message', methods=['POST'])
def handle_message():
    try:
        data = request.json
        message = data.get("message", "")
        status = data.get("status", "success")
        
        if status == "success":
            logging.info(f"[SUCCESS] {message}")
        else:
            logging.error(f"[ERROR] {message}")
        
        return jsonify({"status": "success", "message": "Message received"}), 200
    except Exception as e:
        logging.error(f"[ERROR] 메시지 처리 중 예외 발생: {str(e)}")
        return jsonify({"status": "error", "message": "Error handling message"}), 500

if __name__ == "__main__":
    # WebSocket 클라이언트 인스턴스 생성 및 연결
    client = VendingMachineClient()
    client.connect()

    # Flask 서버 실행
    flask_thread = threading.Thread(target=lambda: app.run(host="127.0.0.1", port=8081, debug=False), daemon=True)
    flask_thread.start()

    try:
        while True:
            command = input("명령어 입력 (exit): ").strip().lower()
            if command == "exit":
                break
    except KeyboardInterrupt:
        logging.info("프로그램 종료")
    finally:
        client.disconnect()
