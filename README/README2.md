##### **게임에 대한 간단한 소개:**

**수박 게임 -  작은 과일을 합쳐 가장 큰 수박을 만드는 게임**



##### **현재까지의 진행 상황:**

**1주차: 기본 프레임워크 구축 및 리소스 조사: 90% (리소스 추후에 수정/추가 예정)**

**2주차: 화면에 과일을 떨어뜨리고 굴러가는 물리 환경 구축: 70% (아직 과일이 사각형이라 굴러가지는 않음)**

**3주차: 충돌 감지 및 과일 레벨 시스템 생성: 100%**

**4주차: 과일 합성 및 연쇄 반응 구현: 100%**



##### **git commit**



##### **목표가 변경된 내용**

**추후 과일들을 원형이 아닌 사각형으로 배치할 가능성이 존재.  최대한 방법을 찾아보려고 노력.**



##### **Activity 구성**

**MainActivity와 SuikaGameActivity로 구성.**

**MainActivity에서 게임 시작 버튼을 누르면 SuikaGameActivity를 실행하도록 함.**



##### **Scene 구성 및 전환 관계**

**SuikaGameActivity에 들어오면 MainScene으로 전환.**



##### **MainScene game object**

**Background: 배경**

**Floor: 바구니의 바닥**

**CollisionChecker: 충돌 체크**

**Fruit: 과일**



##### **class 구성 정보**

###### **MainScene:**

* **월드에 object 추가:  override val world = World(...)**
* **과일 랜덤 생성:  private fun spawnNextFruit**
* **터치 이벤트(과일 드래그\&드랍):  override fun onTouchEvent**



###### **Background:** 

* **배경 초기화: init**



###### **Floor:** 

* **바닥 초기화: init**
* **충돌박스 생성: override val collisionRect**



###### **Fruit:**  

* **과일 이미지 인덱스를 받아 초기화: init**
* **충돌박스 생성: override val collisionRect**
* **과일 위치:  fun setPosition (기능은 a2dg/Sprite의 setCenter와 같으나 protect라 다른 곳에서 사용불가능해 정의)**
* **튕김 처리: fun bounceBack**



###### **CollisionChecker:**

* **과일 간 충돌 체크, 충돌한 과일은 알맞게 처리: override fun update**



##### **UX 진행 방법**

**일정대로 6\~7주차에 점수 시스템과 다음에 나올 과일 미리보기 추가 예정**



##### **까다로웠던 부분**

**아래의 과일이 합쳐지면 그 위에 있던 과일이 falling 되지 않는 문제를 해결하는 것이 어려웠음 (해결)**

**과일을 원형의 이미지로 자르고 충돌 박스까지 씌우는 부분이 어려웠음 (미해결)** 

