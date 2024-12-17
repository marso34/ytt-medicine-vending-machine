from time import sleep
import sys
import subprocess
import os

# 내부파일 실행
def execute_file(file_path):
    if os.path.isfile(file_path):
        try:
            subprocess.run(["python3", file_path], check=True)
            print(f"[INFO] {file_path} 실행 완료!")
        except Exception as e:
            print(f"[ERROR] {file_path} 실행 중 오류 발생:", e)
    else:
        print(f"[ERROR] 파일 {file_path}이(가) 존재하지 않습니다.")

def main():
    if len(sys.argv) > 1:
        locker_number = sys.argv[1]
        print(f"[INFO] 전달된 locker_number: {locker_number}")
        
        # locker_number를 이용한 작업 추가
        execute_file("/home/capstone/order/moter/storage_out/{locker_number}_out.py")
        print("---------------------------------------------------------------------------")
        print("{locker_number}번 보관함 작동합니다!!!!!!")
        print("---------------------------------------------------------------------------")
        sleep(5)  # 5초 대기

    else:
        print("[WARNING] locker_number가 전달되지 않았습니다.")

if __name__ == "__main__":
    main()
