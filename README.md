# 개발환경
android sdk 17, android gradle plugin version 8.5.0
# 내용
## 1. RecyclerView Sample
여러게 뷰 홀더 샘플
## 2. Todo
할일 추가/수정/삭제, 알림 등록, 지정된 날짜 알림 확인
## 3. music Palyer
Figma Sample 따라해보고 music 리스트 플레이, Room DB, MediaPlayer 사용
## 4. chatting app
Figma Sample 따라해보고 채팅 앱 만들어보기 activity는 fragment로 화면 전환  Room DB 사용
## 5. QR, Barcode 촬영
QR코드나 Barcode를 촬영하여 데이터 확인
## 6. 설치된 어플리케이션 목록 list
어플리케이선 목록 조회, 카테고리 구분, 디자인 적용
## 7. Map Sdk 사용해보기
google map, naver map 맵 사용해보고 위치 정보 찍어보기


### 개발 시 참고할 만한 사이트들

## Android Studio 소소 팁
[MarkDown Preview 보이기](https://ogyong.tistory.com/49)

## Plugin
[kdoc-generator] 메서즈 주석 자동 생성, Kotlin 호환 확인   

## 디자인 (참고용)  
[팬톤(pantone): 올해의 색상](https://www.pantone.com/hk/en/color-of-the-year/2024)   
[구글 디자인 가이드: 멀티리얼 가이드, 24년도 기준 m3 출시](https://m3.material.io/components/lists/overview)   

## 디자인 (설계, 작업)
[Figma: 개발 할거를 구현하기전에 어떻게 만들지 정리](https://www.figma.com)   
[웹 포토샵: 이미지 제작/수정](https://pixlr.com/kr/editor/)   
[웹 포토샵2: 앱에서 하는것처럼 간단하고 자주 만지는것 위주](https://fotoram.io/editor/)

## 디자인 (가끔사용, 유용)
[UNSCREEN: 배경 투명 처리(이미지, 동영상)](https://www.unscreen.com/)   
[이미지 파일 색상 추출](http://www.cssdrive.com/imagepalette/index.php)   
[앱 아이콘 생성: 앱 마켓 아이콘용](https://appiconmaker.co/)   
[무료 배경이미지](https://www.shutterstock.com/ko/?c3apidt=p52913990603&gclid=CjwKCAjw4_H6BRALEiwAvgfzqy_YoX_wjDMewTbyQ9SOn7dKmpGiBxHbh2tfSl5fekycO07yroNNDBoCssMQAvD_BwE&gclsrc=aw.ds&kw=unsplash)   
[무료 아이콘](https://www.flaticon.com/kr/uicons/interface-icons)   


리스트 가이드 참고 
https://m3.material.io/components/lists/specs


APK 서명
```zsh
sh sign_v2.sh apk-release.apk
```