# 개발자 환경 구성

개발자 환경에서 개발에 필요한 DB, Redis 솔루션은 Docker 로 설치하여 사용합니다.

## Files

* docker-compose.yaml: docker compose 설정 파일
* initdb.sh: PostgreSQL 의 초기 설정 실행 파일

## 실행

* `docker compose up -d` 명령어로 DB 와 Redis 를 실행합니다.

## 중단

* `docker compose down -v` 명령어오 DB 와 Redis 를 중지하고, DB 볼륨을 삭제합니다.
* DB 데이터 초기화가 불필요한 경우에는 `-v` 옵션을 삭제하고 사용합니다.

## DB 초기 설정 변경

* PostgreSQL 초기 설정 변경 사항은 `initdb.sh` 에 표기하여 사용합니다.
* 이전에 PostgreSQL 을 실행한 적이 있다면 `docker compose down -v` 를 이용하여 이전의 볼륨을 삭제 해주어야 초기 설정 변경 사항이 적용 됩니다. 
