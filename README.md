# 확장자 차단 (Extension Blocker)

파일 업로드 시 차단할 확장자를 관리하는 설정 화면입니다.

## Live

- 프론트엔드: https://hacanna42.github.io/employment-flow-extension-blocker/
- 백엔드 API: https://extension-blocker.seung.lol

## 기술 스택

| 구분 | 내용 |
|---|---|
| 프론트엔드 | React 18, TypeScript, Vite 6 |
| 백엔드 | Spring Boot 4.1, Java 25, Spring Data JPA, Flyway |
| DB | PostgreSQL |
| 배포 | GitHub Actions → GitHub Pages (프론트), AWS EC2 systemd + nginx (백엔드) |

## 프로젝트 구조

```
.
├── frontend/
├── backend/
└── .github/
```

## 로컬 셋업

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```
백엔드 없이 프론트만 보려면 `.env.development`에서 `VITE_USE_MOCK=true` (localStorage로 동작).

```bash
cd backend
docker compose up -d
DB_USERNAME=postgres DB_PASSWORD=postgres ./gradlew bootRun   # http://localhost:8080
```
