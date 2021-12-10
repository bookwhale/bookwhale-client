# bookwhale-client


## 패키지 구조

| 패키지 | 상세 |
| ------ | ------ |
| data | 저장소, 네트워크, 로컬DB 관련 |
| di | 의존성 주입 파일 관련 |
| model | 데이터모델, DTO |
| screen | View, ViewModel |
| widget | 위젯 관련 (adapter 등) |
| util | 그 외 유틸 |

## MVVM

- [View] - ViewModel에 접근하여 data를 이용하여 UI변경 가능
- [ViewModel] - Model을 이용, Repository에 접근하여 데이터를 가공
- [Model] - Repository와 ViewModel사이에 주고받는 데이터 관리
- [Repository] - 로컬, 혹은 네트워크 저장소
- [DI] - 객체를 필요한곳에 직접 전달


## FLOW

- ViewModel에서 Repository등과 통신하며 LiveData를 이용하여 데이터를 가공한다. 
- Repository는 Entity의 형태로 데이터를 주고받으며, ViewModel에서 해당 Entity를 필요에 맞게 사용하기 위해 Model로 매핑하여 사용한다.
- View에서 Observing 패턴을 사용하여 LiveData를 관찰하며 UI를 갱신한다.
- 이 과정에서 필요한 객체들 (ViewModel에서 필요한 repository등)은 Koin을 이용하여 직접 주입한다.

