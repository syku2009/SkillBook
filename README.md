# SkillBook Plugin

MythicMobs 연동 스킬북 & 키바인딩 스킬 시스템 플러그인

## 요구사항

- Minecraft 1.20.1
- Paper 서버
- MythicMobs 플러그인
- Java 17+

## 빌드 방법

```bash
cd SkillBookPlugin
mvn clean package
```

빌드된 JAR 파일은 `target/SkillBookPlugin-1.0.0.jar` 에서 찾을 수 있습니다.

## 설치 방법

1. 빌드된 JAR 파일을 서버의 `plugins` 폴더에 복사
2. 서버 재시작
3. MythicMobs에 스킬이 등록되어 있어야 합니다

## 명령어

| 명령어 | 설명 | 권한 |
|--------|------|------|
| `/스킬북 <스킬ID>` | 스킬북 아이템 지급 | skillbook.admin |
| `/스킬리스트` | 보유 스킬 목록 GUI | skillbook.use |
| `/스킬 리로드` | 스킬 정보 리로드 | skillbook.admin |

## 사용 방법

1. `/스킬북 <스킬ID>` 명령어로 스킬북 지급
2. 스킬북을 우클릭하여 스킬 습득
3. `/스킬리스트`로 GUI를 열어 키 바인딩 설정
4. 설정한 키를 입력하여 스킬 사용

## 키 바인딩

- **우클릭**: 맨손 우클릭
- **좌클릭**: 맨손 좌클릭
- **쉬프트+우클릭**: 웅크리며 우클릭
- **쉬프트+좌클릭**: 웅크리며 좌클릭
- **쉬프트 두 번**: 빠르게 쉬프트 2회

## 설정 파일

### config.yml

```yaml
messages:
  skill-not-found: "§c존재하지 않는 스킬입니다."
  skill-already-learned: "§c이미 습득한 스킬입니다."
  skill-learned: "§a스킬을 습득했습니다: %skill%"
  cooldown-remaining: "§c아직 %time%초 남았습니다."

double-shift-interval: 300  # 더블 쉬프트 감지 간격 (ms)
cmd-start: 30000           # CustomModelData 시작 번호
```

## 파일 구조

```
plugins/SkillBookPlugin/
├── config.yml           # 설정 파일
├── skill_cmd_map.yml    # 스킬-CMD 매핑 (자동 생성)
└── playerdata/          # 플레이어 데이터
    └── <UUID>.yml
```

## 라이선스

MIT License
