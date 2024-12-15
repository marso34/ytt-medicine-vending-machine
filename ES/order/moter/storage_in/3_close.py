# 보관함 3번 각도 조정 (95도)
import RPi.GPIO as GPIO     # 라즈베리파이 GPIO 관련 모듈을 불러옴
from time import sleep      # time 라이브러리의 sleep 함수 사용

GPIO.setmode(GPIO.BCM)      # GPIO 핀들의 번호를 지정하는 규칙 설정

# setup 부분
servo_pin = 25
GPIO.setup(servo_pin, GPIO.OUT)  # 서보핀을 출력으로 설정 
servo = GPIO.PWM(servo_pin, 50)  # 서보핀을 PWM 모드 50Hz로 사용
servo.start(0)                   # 서보모터의 초기값을 0으로 설정

servo_min_duty = 3               # 최소 듀티비를 3으로
servo_max_duty = 12              # 최대 듀티비를 12로

def set_servo_degree(degree):    # 각도를 입력하면 듀티비를 설정해주는 함수
    if degree > 180:
        degree = 180
    elif degree < 0:
        degree = 0

    duty = servo_min_duty + (degree * (servo_max_duty - servo_min_duty) / 180.0)
    servo.ChangeDutyCycle(duty)
    sleep(0.5)  # 잠시 대기하여 서보가 해당 위치로 움직일 시간 확보
    servo.ChangeDutyCycle(0)  # 서보 모터가 더이상 움직이지 않도록 듀티비를 0으로 설정

# main 루프 부분
try:
    set_servo_degree(95)  # 서보 모터를 90도로 한번만 움직임

finally:
    GPIO.cleanup()  # GPIO 핀들을 초기화
