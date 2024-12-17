import RPi.GPIO as GPIO     # 라즈베리파이 GPIO 관련 모듈을 불러옴
import time                 # 시간관련 모듈을 불러옴

GPIO.setmode(GPIO.BCM)      # GPIO 핀들의 번호를 지정하는 규칙 설정

### 이부분은 아두이노 코딩의 setup()에 해당합니다
LED_pin = 20                     # LED 핀은 라즈베리파이 GPIO 2번핀으로 
GPIO.setup(LED_pin, GPIO.OUT)   # LED 핀을 출력으로 설정

### 이부분은 아두이노 코딩의 loop()에 해당합니다
start_time = time.time()        # 프로그램 시작 시간 기록

try:                                    # 이 try 안의 구문을 먼저 수행하고
    while True:                         # 무한루프 시작: 아두이노의 loop()와 같음
        # 10초가 경과했는지 확인
        if time.time() - start_time > 10:
            print("10초가 지나 프로그램이 종료됩니다.")
            break

        GPIO.output(LED_pin, GPIO.HIGH) # LED 핀에 HIGH 신호 인가(LED 켜짐)
        time.sleep(1)                   # 1초간 대기 : 아두이노의 delay(1000)과 같음
        GPIO.output(LED_pin, GPIO.LOW)  # LED 핀에 LOW 신호 인가(LED 꺼짐)
        time.sleep(1)                   # 1초간 대기

### 이부분은 반드시 추가해주셔야 합니다.
finally:                                # try 구문이 종료되면
    GPIO.cleanup()                      # GPIO 핀들을 초기화