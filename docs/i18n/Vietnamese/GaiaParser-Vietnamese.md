# GAIA (GS1 Application Identifiers Analyser) — Hướng dẫn cho lập trình viên

## Mục lục

1. [Tổng quan](#tổng-quan)
2. [Về GS1 và General Specifications](#về-gs1-và-general-specifications)
3. [Mã nhận dạng ứng dụng GS1](#mã-nhận-dạng-ứng-dụng-gs1)
4. [Hướng dẫn nhanh](#hướng-dẫn-nhanh)
5. [Chuỗi xử lý phân tích](#chuỗi-xử-lý-phân-tích)
   - [Giai đoạn tiền xử lý — Bộ sửa đổi đầu vào](#giai-đoạn-tiền-xử-lý--bộ-sửa-đổi-đầu-vào)
   - [Giai đoạn 0 — Mã tương quan](#giai-đoạn-0--mã-tương-quan)
   - [Giai đoạn 1 — Định tuyến đầu vào](#giai-đoạn-1--định-tuyến-đầu-vào)
   - [Giai đoạn 2 — Cú pháp](#giai-đoạn-2--cú-pháp)
   - [Giai đoạn 3 — Nội dung](#giai-đoạn-3--nội-dung)
   - [Giai đoạn 4 — Diễn giải](#giai-đoạn-4--diễn-giải)
6. [Cấu hình phân tích (`ParseConfig`)](#cấu-hình-phân-tích-parseconfig)
   - [Các tùy chọn](#các-tùy-chọn)
   - [Thông báo và nhãn đã bản địa hóa](#thông-báo-và-nhãn-đã-bản-địa-hóa)
   - [Định dạng ngày](#định-dạng-ngày)
7. [Bộ sửa đổi đầu vào](#bộ-sửa-đổi-đầu-vào)
   - [Các bộ sửa đổi có sẵn](#các-bộ-sửa-đổi-có-sẵn)
   - [Viết một bộ sửa đổi](#viết-một-bộ-sửa-đổi)
   - [Đăng ký bộ sửa đổi](#đăng-ký-bộ-sửa-đổi)
   - [Xem bộ sửa đổi đã làm gì](#xem-bộ-sửa-đổi-đã-làm-gì)
   - [Xử lý thất bại của bộ sửa đổi](#xử-lý-thất-bại-của-bộ-sửa-đổi)
8. [Chế độ phân tích](#chế-độ-phân-tích)
   - [Chế độ DATA_CARRIER](#chế-độ-data_carrier)
   - [Chế độ SYNTAX](#chế-độ-syntax)
   - [Chế độ CONTENT](#chế-độ-content)
   - [Chế độ INTERPRETATION (mặc định)](#chế-độ-interpretation-mặc-định)
9. [Mã tương quan](#mã-tương-quan)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Làm việc với kết quả](#làm-việc-với-kết-quả)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry và DataCarrierType](#datacarrierentry-và-datacarriertype)
12. [Tham chiếu lỗi](#tham-chiếu-lỗi)
13. [An toàn luồng](#an-toàn-luồng)
14. [Phụ lục A — Hằng số chuỗi AI](#phụ-lục-a--hằng-số-chuỗi-ai)
    - [Nhận dạng và đánh số sê-ri](#nhận-dạng-và-đánh-số-sê-ri)
    - [Ngày và giờ](#ngày-và-giờ)
    - [Số lượng và phép đo — Khối lượng thay đổi (hệ mét)](#số-lượng-và-phép-đo--khối-lượng-thay-đổi-hệ-mét)
    - [Số lượng và phép đo — Khối lượng thay đổi (hệ Anh / Mỹ)](#số-lượng-và-phép-đo--khối-lượng-thay-đổi-hệ-anh--mỹ)
    - [Giá và số tiền](#giá-và-số-tiền)
    - [Địa điểm và vận chuyển](#địa-điểm-và-vận-chuyển)
    - [Thuộc tính sản phẩm và khả năng truy xuất nguồn gốc](#thuộc-tính-sản-phẩm-và-khả-năng-truy-xuất-nguồn-gốc)
    - [Số hoàn trả y tế quốc gia (NHRN)](#số-hoàn-trả-y-tế-quốc-gia-nhrn)
    - [Y tế, GMN, HIDRI, CPID và dữ liệu về người](#y-tế-gmn-hidri-cpid-và-dữ-liệu-về-người)
    - [Dùng nội bộ / trong công ty](#dùng-nội-bộ--trong-công-ty)
15. [Phụ lục B — Hằng số khóa diễn giải](#phụ-lục-b--hằng-số-khóa-diễn-giải)
    - [Ngày và giờ](#ngày-và-giờ)
    - [Ngày thu hoạch](#ngày-thu-hoạch)
    - [Tiền tố công ty GS1](#tiền-tố-công-ty-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Quốc gia (ISO 3166)](#quốc-gia-iso-3166)
    - [Tiền tệ (ISO 4217)](#tiền-tệ-iso-4217)
    - [Nhiệt độ](#nhiệt-độ)
    - [Giới tính (ISO 5218)](#giới-tính-iso-5218)
    - [Loài thủy sinh (FAO)](#loài-thủy-sinh-fao)
    - [Số hiệu kho NATO (NSN)](#số-hiệu-kho-nato-nsn)
    - [Sản phẩm dạng cuộn](#sản-phẩm-dạng-cuộn)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Mã nhận dạng SIM (EID / ICCID)](#mã-nhận-dạng-sim-eid--iccid)
    - [Số hiệu chứng nhận](#số-hiệu-chứng-nhận)
    - [GS1 UIC](#gs1-uic)
    - [Thứ tự sinh của trẻ sơ sinh](#thứ-tự-sinh-của-trẻ-sơ-sinh)
    - [Số hiệu mẫu toàn cầu (GMN)](#số-hiệu-mẫu-toàn-cầu-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Giá trị thập phân và giá trị đo lường](#giá-trị-thập-phân-và-giá-trị-đo-lường)
    - [Tọa độ địa lý](#tọa-độ-địa-lý)
    - [Phương pháp sản xuất](#phương-pháp-sản-xuất)
    - [Loại vật trung gian AIDC](#loại-vật-trung-gian-aidc)
    - [Phần trên tổng](#phần-trên-tổng)
    - [Tách thành phần](#tách-thành-phần)
    - [Khác](#khác)

---

## Tổng quan

`GaiaParser` là điểm vào để phân tích chuỗi phần tử Mã nhận dạng ứng dụng (AI) GS1. Nó nhận đầu ra thô của máy quét ở bất kỳ dạng nào sau đây và trả về một `ParseResult` có cấu trúc, chứa mọi AI đã được giải mã, các lỗi kiểm tra hợp lệ, và tùy chọn cả những diễn giải mà con người đọc được:

- Chuỗi phần tử AI đơn thuần: `0109506000134352`
- Chuỗi phần tử có tiền tố Mã nhận dạng hệ ký hiệu AIM: `]C10109506000134352`
- URI GS1 Digital Link: `https://example.com/01/09506000134352`
- Bất kỳ dạng nào ở trên, tùy chọn thêm tiền tố mã tương quan 8 chữ số: `12345678~0109506000134352`

**Lớp điểm vào:** `tools.pantheum.gaia.GaiaParser`

> **Mới biết đến Gaia?** Hãy bắt đầu từ **[Hướng dẫn nhanh GaiaParser](GaiaParser-QuickStart-Vietnamese.md)** — mười phút đi qua các phụ thuộc, lần phân tích đầu tiên, và vài cái bẫy thường gặp nhất. Tài liệu này là bản tham chiếu đầy đủ.

> Chiều ngược lại — *dựng* chuỗi phần tử hợp lệ và URI Digital Link từ các cặp AI/giá trị — được trình bày trong **[GaiaBuilder — Hướng dẫn cho lập trình viên](GaiaBuilder-Vietnamese.md)**.

---

## Về GS1 và General Specifications

**GS1** là một tổ chức phi lợi nhuận toàn cầu xây dựng và duy trì các tiêu chuẩn mở cho việc nhận dạng và trao đổi dữ liệu trong chuỗi cung ứng. Các tiêu chuẩn của tổ chức này được dùng trong bán lẻ, y tế, hậu cần, dịch vụ ăn uống và nhiều ngành khác, bao trùm mọi thứ từ mã vạch sản phẩm trên bao bì tiêu dùng cho đến việc theo dõi từng liều thuốc theo số sê-ri.

Nguồn tham chiếu chính thức cho mọi điều mà bộ phân tích này hiện thực là **GS1 General Specifications** — một tài liệu duy nhất định nghĩa:

- Toàn bộ mã Mã nhận dạng ứng dụng (AI), tiêu đề dữ liệu, định dạng và quy tắc kiểm tra hợp lệ của chúng
- Các quy tắc cú pháp để tạo và mã hóa chuỗi phần tử AI
- Yêu cầu về hệ ký hiệu mã vạch và việc cấp phát Mã nhận dạng AIM
- Thuật toán chữ số kiểm tra và ký tự kiểm tra
- Cách xác định năm hai chữ số (quy tắc cửa sổ trượt)
- Đặc tả của Data Matrix, QR Code, GS1-128, GS1 DataBar và các vật mang khác

GS1 General Specifications được cập nhật hằng năm. Ấn bản hiện hành và tài liệu bổ trợ có tại:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA hiện thực **Bản phát hành 26.0 (đã phê chuẩn, tháng 1/2026)** của GS1 General Specifications.

URI GS1 Digital Link chịu sự điều chỉnh của một tiêu chuẩn đi kèm, **GS1 Digital Link: URI Syntax**, vốn định nghĩa các khóa nhận dạng chính, thứ tự của các bổ ngữ khóa, và cách mã hóa thuộc tính dữ liệu mà bộ phân tích áp dụng cho đầu vào Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA hiện thực **Bản phát hành 1.7.0 (đã phê chuẩn, tháng 8/2026)** của tiêu chuẩn GS1 Digital Link: URI Syntax.

Các tham chiếu mục trong tài liệu này trỏ tới GS1 General Specifications (ví dụ "Table 7-5", "section 7.12"), ngoại trừ số mục của Digital Link (ví dụ "§4.9", "§4.12") vốn trỏ tới tiêu chuẩn GS1 Digital Link: URI Syntax.

---

## Mã nhận dạng ứng dụng GS1

**Mã nhận dạng ứng dụng (AI) GS1** là một tiền tố số ngắn — từ hai đến bốn chữ số — xác định ý nghĩa và định dạng của dữ liệu đi ngay sau nó. Các AI được định nghĩa trong GS1 General Specifications và bao trùm một phạm vi rộng dữ liệu chuỗi cung ứng: mã nhận dạng sản phẩm, ngày tháng, số lượng, số lô, số sê-ri, phép đo, URL, và nhiều thứ khác.

### Cấu trúc của một phần tử AI

Mỗi phần tử AI gồm hai phần:

```
┌─────────────┬──────────────────────────────────┐
│  AI code    │  Data value                      │
│  (2–4 digits)│                                  │
└─────────────┴──────────────────────────────────┘

Example:
  01  09506000134352
  ^^  ^^^^^^^^^^^^^^
  AI  GTIN-14 value (14 digits, fixed length)
```

Mã AI luôn là chữ số. Giá trị dữ liệu đi ngay sau đó, giữa mã và giá trị không có dấu phân tách nào.

### AI độ dài cố định và AI độ dài thay đổi

Các AI chia thành hai loại:

| Loại | Cách hoạt động | Ví dụ |
|---|---|---|
| **Độ dài cố định** | Số ký tự chính xác, luôn được đọc trọn vẹn | AI `01` (GTIN) — luôn 14 chữ số |
| **Độ dài thay đổi** | Từ 1 đến một số ký tự tối đa; kết thúc bởi dấu phân tách GS hoặc hết đầu vào | AI `10` (Lô) — 1 đến 20 ký tự chữ và số |

Một AI thuộc loại cố định hay thay đổi hoàn toàn do định nghĩa của nó trong đặc tả GS1 quyết định — bộ phân tích không bao giờ phỏng đoán.

### Chuỗi phần tử nhiều AI

Nhiều AI có thể nối lại thành một chuỗi phần tử duy nhất. Các AI độ dài cố định có thể nối trực tiếp vì bộ phân tích luôn biết chính xác phải đọc bao nhiêu ký tự. Các AI độ dài thay đổi bắt buộc phải kết thúc bằng **ký tự GS** (ASCII `0x1D`, trong các hệ ký hiệu mã vạch còn gọi là FNC1) mỗi khi có một AI khác đi sau, để bộ phân tích biết một giá trị kết thúc ở đâu và mã AI kế tiếp bắt đầu từ đâu.

```
Fixed-length AIs — no separator needed:

  0109506000134352  17261231
  ^^^^^^^^^^^^^^^^  ^^^^^^^^
  (01) GTIN-14      (17) Expiry date YYMMDD (also fixed)


Variable-length AI followed by another AI — GS separator required:

  10LOT-001 <GS> 21SN-98765
  ^^^^^^^^^       ^^^^^^^^^^
  (10) Batch/Lot  (21) Serial number
         ↑
     ASCII 0x1D


Mixed — variable before fixed:

  10LOT-001 <GS> 0109506000134352
  ^^^^^^^^^       ^^^^^^^^^^^^^^^^
  (10) Batch/Lot  (01) GTIN-14
```

Trong chuỗi ký tự hằng của Java, hãy viết ký tự GS bằng dãy thoát Unicode `""`.

### Các AI thường gặp

| AI | Tiêu đề dữ liệu | Định dạng | Giá trị ví dụ |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, vùng tiền tệ đơn nhất) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Chữ số thứ tư** của một AI đo lường hoặc giá 4 chữ số mã hóa số chữ số thập phân ngầm định — `3103` là trọng lượng tịnh tính bằng kg với 3 chữ số thập phân (`001500` = 1,500 kg), còn `3102` sẽ đọc chính những chữ số ấy thành 15,00 kg. Cột `Định dạng` ở trên cho thấy định dạng của *dữ liệu*; `getFormatString()` đầy đủ của mỗi AI bao gồm cả chính AI đó (ví dụ `N4+N6` cho `3103`).

### Diễn giải mà con người đọc được (HRI)

Dạng dễ đọc theo quy ước đặt mỗi mã AI trong ngoặc đơn ngay trước giá trị của nó, và chừa một khoảng trắng giữa các phần tử:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Dấu phân tách GS không hiển thị trong HRI. Định dạng này do `GS1AIObject.toHriString()` tạo ra.

### Mã AI bốn chữ số

Một số AI dùng bốn chữ số thay vì hai. Hai chữ số đầu xác định họ AI; chữ số thứ ba và/hoặc thứ tư mang ý nghĩa bổ sung (chẳng hạn vị trí dấu thập phân ngầm định ở các AI đo lường). Bộ phân tích tự xác định mã AI đầy đủ từ chuỗi phần tử — bên gọi luôn làm việc với mã đầy đủ (ví dụ `"3102"`, chứ không phải chỉ `"31"`).

---

## Hướng dẫn nhanh

```java
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.GaiaConstants.ParseMode;
import tools.pantheum.gaia.result.ParseResult;

GaiaParser parser = new GaiaParser();

// Default parse (INTERPRETATION mode)
ParseResult response = parser.parse("01095060001343521726123110LOT-001");

if (response.isValid()) {
    System.out.println(response.getAiObject().toHriString());
    // → (01)09506000134352 (17)261231 (10)LOT-001
} else {
    response.getErrors().forEach(e -> System.out.println(e.getMessage()));
}
```

> **Dấu phân tách GS:** Các AI độ dài thay đổi bên trong một chuỗi nhiều AI bắt buộc phải được ngăn cách bằng ký tự GS (ASCII `0x1D`). Hãy dùng `""` trong chuỗi ký tự hằng của Java.

---

## Chuỗi xử lý phân tích

### Giai đoạn tiền xử lý — Bộ sửa đổi đầu vào

Nếu `ParseConfig` mang theo bất kỳ **bộ sửa đổi đầu vào** nào, chúng chạy trước mọi thứ khác — trước khi tách tiền tố tương quan, trước khi nhận diện vật mang, trước khi bước vào chuỗi xử lý GS1. Mỗi bộ sửa đổi viết lại đầu vào thô cho bộ kế tiếp, và mọi giai đoạn bên dưới đều làm việc trên kết quả của chuỗi ấy.

Theo mặc định không có bộ sửa đổi nào được cấu hình, nên giai đoạn tiền xử lý này không làm gì cả trừ khi bạn tự bật lên. Xem [Bộ sửa đổi đầu vào](#bộ-sửa-đổi-đầu-vào).

---

### Giai đoạn 0 — Mã tương quan

Trước bất kỳ xử lý GS1 nào, `GaiaParser` kiểm tra xem đầu vào có bắt đầu bằng một **tiền tố mã tương quan** tùy chọn hay không: đúng 8 chữ số thập phân ASCII rồi đến một dấu ngã (`~`), ví dụ `12345678~`.

Nếu tiền tố có mặt, nó được tách ra và lưu thành một `CorrelationInfo` trên `ParseResult` trả về. Mọi giai đoạn sau đó làm việc trên phần tải đã tách tiền tố. Nếu không có tiền tố, đầu vào đi qua nguyên vẹn.

Xem [Mã tương quan](#mã-tương-quan) để biết chi tiết.

---

### Giai đoạn 1 — Định tuyến đầu vào

Sau khi tách tiền tố tương quan, `GaiaParser` kiểm tra xem đầu vào (đã tách) có bắt đầu bằng một **Mã nhận dạng AIM** hay không: một tiền tố ba ký tự dạng `]` + chữ cái ASCII + chữ số ASCII (ví dụ `]C1` cho GS1-128, `]d2` cho GS1 DataMatrix, `]e0` cho GS1 DataBar / GS1 Composite).

```
Input
  │
  ├─ input modifiers configured? ──YES──► run chain in order ──► ModifierInfo stored
  │
  ├─ starts with DDDDDDDD~ ──► strip correlation prefix ──► CorrelationInfo stored
  │
  ├─ starts with AIM Code ID? ──YES──► DataCarrierParser
  │                                         │
  │                                    Validate carrier
  │                                    Strip prefix + ECI
  │                                    Pad GTIN if needed
  │                                         │
  │                                    GS1Parser
  │                                    (see below)
  │
  ├─ starts with http:// or https:// ──YES──► GS1DLParser
  │
  └─ otherwise ───────────────────────────► GS1AIParser
```

Nếu vật mang không có khả năng chứa AI GS1 (ví dụ một mã vạch bưu chính), việc phân tích dừng ngay lập tức với lỗi `GE-D002`.

---

### Giai đoạn 2 — Cú pháp

Luôn luôn chạy. Gồm hai bước con:

**2a. Tách token (`AISyntaxParser`)**
- Đọc độ dài mã AI từ hai ký tự đầu bằng bảng tiền tố GS1 (GS1 General Specifications Table 7-5).
- AI độ dài cố định đọc đúng số byte quy định từ đầu vào.
- AI độ dài thay đổi được đọc cho tới một ký tự GS hoặc hết đầu vào.
- AI nhiều thành phần có khối giá trị được cắt thành từng đoạn theo thành phần.

**2b. Kiểm tra hợp lệ về cấu trúc (`SyntaxValidator`)**
- Kiểm tra AI trùng lặp (`GE-S004`).
- Kiểm tra các phụ thuộc AI bắt buộc, ví dụ AI `02` cần AI `37` (`GE-S005`).
- Kiểm tra các cặp AI bị loại trừ (`GE-S006`).

Lỗi ở giai đoạn này có mức `SYNTAX_ERROR` (bộ tách token) hoặc `INTEGRITY_ERROR` (cấu trúc). Nếu có **bất kỳ** lỗi nào — của bộ tách token hay của cấu trúc — chuỗi xử lý dừng lại và các giai đoạn nội dung cùng diễn giải bị bỏ qua.

---

### Giai đoạn 3 — Nội dung

Chỉ chạy khi Giai đoạn 2 không sinh ra lỗi nào (cả bộ tách token lẫn cấu trúc). Chuỗi xử lý theo từng phần tử (mỗi bước chỉ chạy nếu bước trước không sinh lỗi):

| Bước | Bộ kiểm tra | Mã lỗi |
|---|---|---|
| Kiểm tra biểu thức chính quy | `RegexValidator` | `GE-C001` |
| Tập ký tự và định dạng của thành phần | `ComponentValidator` | `GE-C005` + mã định dạng theo từng điều kiện (`GE-C054`–`GE-C115`) |
| Chữ số kiểm tra / ký tự kiểm tra | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Kiểm tra ngữ nghĩa riêng | `ContentValidatorRegistry` | mã nội dung theo từng điều kiện (`GE-C116`–`GE-C170`) |

Lỗi ở giai đoạn này có mức `FORMAT_ERROR` hoặc `DATA_ERROR`, với một ngoại lệ: các phép kiểm tra
tiền tố công ty GS1 trên những AI mang khóa GS1 chỉ mang tính khuyến cáo và có mức `WARNING`
(xem [Tham chiếu lỗi](#tham-chiếu-lỗi)), nên một tiền tố công ty không nhận ra được tự nó
không làm kết quả trở nên không hợp lệ.

---

### Giai đoạn 4 — Diễn giải

Chỉ chạy ở chế độ `INTERPRETATION` và chỉ khi không phần tử nào mang lỗi từ bất kỳ giai đoạn trước đó. `InterpretationEngine` làm giàu mỗi phần tử bằng siêu dữ liệu có nhãn:

- Ngày tháng được định dạng lại thành `dd/mm/yyyy`
- Phân rã chữ số kiểm tra của GTIN và tra cứu tiền tố công ty GS1
- Tên quốc gia theo ISO 3166
- Tên và ký hiệu tiền tệ theo ISO 4217
- Số tiền thập phân đã giải mã
- Các mảnh HRI (Diễn giải mà con người đọc được)

Kết quả được gắn vào mỗi `GS1AIObjectElement` dưới dạng các mục `GS1AIInterpretation`.

---

## Cấu hình phân tích (`ParseConfig`)

`GaiaParser` cung cấp đúng hai điểm vào:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` chạy với **cấu hình mặc định**: chế độ `INTERPRETATION`, ngày theo thứ tự nhỏ trước (`dd/mm/yyyy`) với dấu phân tách `/` và năm bốn chữ số, cùng thông báo lỗi bằng **tiếng Anh**. Để thay đổi bất kỳ điều nào trong số đó — kể cả chế độ phân tích — hãy dựng một `ParseConfig` bằng bộ dựng nối chuỗi của nó và dùng phiên bản hai tham số.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Toàn bộ enum của các tùy chọn đều nằm trong `GaiaConstants`.

### Các tùy chọn

| Phương thức của bộ dựng | Enum (`GaiaConstants`) | Mặc định | Tác dụng |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Độ sâu của chuỗi xử lý — xem [Chế độ phân tích](#chế-độ-phân-tích). |
| `language(...)`      | `Language`      | `ENGLISH`        | Ngôn ngữ của thông báo lỗi, nhãn diễn giải **và** mô tả AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Thứ tự các thành phần ngày: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Ký tự giữa các thành phần ngày: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) hoặc `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) hoặc `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Bỏ qua phép kiểm tra cấu trúc "yêu cầu" (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Bỏ qua phép kiểm tra cấu trúc "loại trừ" (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / tên lớp | không có | Mã viết lại đầu vào thô trước khi phân tích — hai [bộ sửa đổi có sẵn](#các-bộ-sửa-đổi-có-sẵn) cùng bất kỳ bộ nào bạn tự viết. Xem [Bộ sửa đổi đầu vào](#bộ-sửa-đổi-đầu-vào). |

Bốn tùy chọn về ngày chỉ ảnh hưởng tới các chuỗi ngày đã định dạng do bộ làm giàu diễn giải tạo ra (ở chế độ `INTERPRETATION`); chúng không thay đổi việc kiểm tra hợp lệ. Có thể bỏ qua các giá trị của bộ dựng — tùy chọn nào không được đặt (hoặc được truyền `null`) sẽ giữ nguyên giá trị mặc định.

### Thông báo và nhãn đã bản địa hóa

`language(...)` chọn ngôn ngữ cho **ba** loại văn bản mà con người đọc: thông báo lỗi, nhãn diễn giải (`getLabel()` của mỗi `GS1AIInterpretation`), và mô tả AI (`getDescription()` của mỗi `GS1AIObjectElement`).

`GaiaConstants.Language` định nghĩa **35 ngôn ngữ**, bao trùm những ngôn ngữ được nói nhiều nhất thế giới: tiếng Anh, Pháp, Tây Ban Nha, Đức, Ý, Bồ Đào Nha, Hà Lan, Ba Lan, Nga, Ukraina, Séc, Thụy Điển, Trung, Nhật, Hàn, Ả Rập, Indonesia, Hindi, Thổ Nhĩ Kỳ, Bengal, Urdu, Việt, Pidgin Nigeria, Ả Rập Ai Cập, Marathi, Telugu, Tamil, Quảng Đông, Ngô, Tagalog, Ba Tư, Hausa, Punjab, Java và Swahili.

Tình trạng dịch thuật (như khi phát hành):
- **Nhãn diễn giải** — đã dịch cho mọi ngôn ngữ.
- **Thông báo lỗi** — đã dịch cho mọi ngôn ngữ.
- **Mô tả AI** — đã dịch cho mọi ngôn ngữ trừ tiếng Anh. Tiếng Anh không phải một danh mục riêng: nó được đọc thẳng từ trường `description` trong mục của AI đó ở `gs1-application-identifiers.jsonld`, và rốt cuộc mọi mô tả AI đều quay về đây.

Pidgin Nigeria (`NIGERIAN_PIDGIN`), một ngôn ngữ creole trên nền tiếng Anh, cố ý dùng lại chính văn bản tiếng Anh cho nhãn diễn giải và thông báo lỗi. Mô tả AI là ngoại lệ của ngoại lệ ấy: chúng được dịch sang lối diễn đạt Pidgin thực thụ thay vì dùng lại tiếng Anh, bởi các danh mục mô tả AI được tạo độc lập với danh mục nhãn/thông báo. Nên để người bản ngữ rà soát các bản dịch máy trước khi tin dùng trong môi trường vận hành.

Bất kỳ thông báo, nhãn hay mô tả nào thiếu trong danh mục của một ngôn ngữ đều quay về tiếng Anh. Các ngôn ngữ viết từ phải sang trái (Ả Rập, Urdu, Ả Rập Ai Cập, Ba Tư) được lưu đúng dưới dạng chuỗi; việc hiển thị chúng từ phải sang trái là trách nhiệm của lớp trình bày.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Nhãn diễn giải cũng được bản địa hóa theo cách ấy (giá trị không đổi — chỉ nhãn thay đổi):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Mô tả AI cũng được bản địa hóa theo cách ấy (chỉ riêng `getTitle()`, ví dụ `"GTIN"`, là không bản địa hóa):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Định dạng ngày

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Bộ sửa đổi đầu vào

**Bộ sửa đổi đầu vào** là đoạn mã viết lại chuỗi đầu vào thô trước khi Gaia phân tích nó. Các bộ sửa đổi tồn tại vì thứ đầu vào đến nơi đã méo mó sẵn — một máy quét thay dấu phân tách GS bằng một ký tự in được, một lớp trung gian bọc phần tải trong tiền tố riêng của nhà cung cấp, một hệ thống chủ chuyển mọi thứ sang chữ hoa. Thay vì tiền xử lý từng chuỗi ở mỗi nơi gọi (rồi sai một cách tinh vi ở một trong số đó), hãy đăng ký việc chuẩn hóa một lần trên `ParseConfig` và để bộ phân tích áp dụng nó.

Các bộ sửa đổi chạy ngay đầu `GaiaParser.parse(...)` — trước khi tách mã tương quan, trước khi nhận diện Mã nhận dạng AIM, trước chuỗi xử lý GS1. Mọi thứ phía sau chỉ thấy chuỗi đã được viết lại. **Theo mặc định không có gì được cấu hình**, kể cả hai [bộ sửa đổi có sẵn](#các-bộ-sửa-đổi-có-sẵn) — bạn tự bật cho từng `ParseConfig`.

**Giao diện:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Các bộ sửa đổi có sẵn

Trong jar lõi có sẵn hai bộ sửa đổi, nằm ở `tools.pantheum.gaia.modifier.custom`. Chúng xử lý hai kiểu méo mó thường gặp nhất của phần tải GS1 — dấu ngoặc HRI được in ra rồi bị coi là dữ liệu, và các khoảng trắng thừa — nên những trường hợp phổ biến không cần đến lớp tự viết:

| Lớp | `getName()` | Công dụng |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Bỏ dấu ngoặc HRI quanh mỗi AI (`(01)…(10)…`) và khôi phục dấu phân tách FNC1 mà chúng hàm ý. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Xóa mọi khoảng trắng (`0x20`) khỏi chuỗi phần tử AI. |

Cả hai đều là hiện thực `ModifierInterface` bình thường, không có địa vị đặc biệt nào — được đăng ký, sắp thứ tự, báo cáo và thất bại y hệt như bộ sửa đổi bạn tự viết:

```java
import tools.pantheum.gaia.modifier.custom.ModifierRemoveAIBrackets;
import tools.pantheum.gaia.modifier.custom.ModifierRemoveSpaces;

ParseConfig config = ParseConfig.builder()
        .modifier(new ModifierRemoveSpaces())        // spaces first — see the ordering note
        .modifier(new ModifierRemoveAIBrackets())
        .build();

ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123 ( 17 ) 251231", config);
r.getPayload();   // 010952123454321310ABC123<GS>17251231
r.isValid();      // true
```

Cả hai đều không giữ trạng thái và an toàn luồng, nên có thể dùng chung một thể hiện duy nhất; và cả hai đều có thể gọi tên bằng tên lớp đầy đủ cho các thiết lập dựa trên tệp cấu hình (xem [Đăng ký bộ sửa đổi](#đăng-ký-bộ-sửa-đổi)).

#### `ModifierRemoveAIBrackets`

Diễn giải mà con người đọc được của GS1 in mỗi AI trong ngoặc đơn — `(01)09521234543213(10)ABC123` — thuần túy như một quy ước in ấn. Một máy quét hay lớp trung gian được đặt để phát ra HRI sẽ chuyển tiếp những dấu ngoặc ấy như thể chúng là dữ liệu, và bộ tách token chẳng biết phải làm gì với chúng.

Bỏ dấu ngoặc mới chỉ là một nửa công việc. Trong HRI, chính dấu `(` mở của AI *kế tiếp* mới đánh dấu chỗ kết thúc của giá trị trước đó, nên ở dạng có ngoặc, AI độ dài thay đổi không cần FNC1. Cứ bỏ dấu ngoặc một cách ngây thơ thì ranh giới ấy biến mất:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Vì vậy bộ sửa đổi này **chèn lại một FNC1 tại mỗi ranh giới mà AI đứng trước có độ dài thay đổi**, khôi phục đúng những gì các dấu ngoặc đã mã hóa:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Độ dài được tra trong chính `AiDefinitionRegistry` của bộ phân tích, nên mọi AI độ dài thay đổi đều được xử lý chứ không phải chỉ một danh sách cứng trong mã. Ba trường hợp cố ý được để nguyên: giá trị vốn đã kết thúc bằng FNC1 (nguồn phát ra cả hai quy ước sẽ không nhận thêm dấu phân tách thứ hai), mã trong ngoặc không phải một AI đã biết (AI không rõ chẳng nói gì về độ dài của chính nó), và AI cuối cùng trong chuỗi.

Việc viết lại này có **tính lũy đẳng** — chạy nó trên chính kết quả của nó thì không có gì thay đổi — nên nó an toàn với một luồng dữ liệu hỗn hợp mà chỉ một phần đầu vào có ngoặc.

> **Hạn chế.** Bản thân `(` và `)` là những ký tự dữ liệu GS1 hợp lệ, và mẫu dùng ở đây chỉ là `\((\d{2,4})\)`. Một giá trị tình cờ chứa số hai đến bốn chữ số đặt trong ngoặc thì cũng sẽ bị bóc ngoặc. Chỉ áp dụng nó cho nguồn dùng quy ước ngoặc HRI, chứ không cho nguồn dùng giá trị có ngoặc thật sự.

#### `ModifierRemoveSpaces`

Một số máy quét, lớp trung gian và quy trình in nhãn chèn khoảng trắng thừa vào một chuỗi phần tử vốn dĩ đúng dạng — để đệm cho một trường có bề rộng cố định, để tách các nhóm cho dễ đọc, hoặc để ngắt dòng một giá trị dài. Bộ tách token coi từng khoảng trắng là dữ liệu, làm hỏng giá trị chứa nó và, với AI độ dài thay đổi, đẩy lệch mọi thứ phía sau.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Chỉ ASCII `0x20` bị xóa. Các ký tự trắng khác được giữ nguyên — chẳng hạn ký tự tab nằm ngoài tập ký tự mà GS1 mã hóa được, nên bộ phân tích báo nó là `GE-S008` thay vì lặng lẽ quét nó đi.

> **Hạn chế.** Khoảng trắng (`0x20`) thuộc tập ký tự bất biến của GS1, nên một số lô hay số linh kiện của khách hàng hoàn toàn có thể chứa khoảng trắng một cách chính đáng. Bộ sửa đổi không thể phân biệt khoảng trắng thừa với khoảng trắng thật; chỉ áp dụng nó cho nguồn mà bạn biết chắc là không dùng khoảng trắng bên trong giá trị AI.

#### Tiền tố được bỏ qua chứ không viết lại

Các bộ sửa đổi chạy khi bộ phân tích chưa tách bỏ thứ gì, nên đầu vào thô vẫn có thể mang mã tương quan, Mã nhận dạng AIM và chỉ báo ECI. Cả hai bộ sửa đổi có sẵn đều xác định chỗ bắt đầu của chuỗi phần tử AI bằng chính logic `CorrelationIdParser` và `DataCarrierParser` của bộ phân tích, chỉ viết lại từ đó trở đi, rồi nối kết quả trở lại với phần tiền tố **nguyên vẹn**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Những vật mang EAN/UPC có giá trị được đệm lên GTIN-14 (`isRequiresGtinPadding()`) bị bỏ qua hoàn toàn — phần tải của chúng là giá trị mã vạch bằng số thuần túy, không có cấu trúc AI, nên ở đó dấu ngoặc lẫn khoảng trắng đều không thể mang ý nghĩa gì.

#### Thứ tự: khoảng trắng trước dấu ngoặc

Khi dùng cả hai, hãy **đăng ký `ModifierRemoveSpaces` trước**. Việc khớp dấu ngoặc phụ thuộc vị trí: một `( 01 )` bị nới ra bằng khoảng trắng không khớp với `\((\d{2,4})\)`, nên các dấu ngoặc còn nguyên và dấu phân tách mà chúng hàm ý không bao giờ được khôi phục.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Viết một bộ sửa đổi

Hãy tự viết khi cả hai bộ có sẵn đều không phù hợp — giao diện chỉ có một phương thức.

```java
package com.example.gaia;

import tools.pantheum.gaia.modifier.ModifierInterface;

/** Substitutes the printable {GS} placeholder back to the real separator (ASCII 0x1D). */
public final class GsPlaceholderModifier implements ModifierInterface {

    @Override
    public String modify(String input) {
        return input == null ? null : input.replace("{GS}", "\u001D");
    }
}
```

Hãy ghi đè phiên bản hai tham số khi việc viết lại phụ thuộc vào cấu hình phân tích:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Giao ước:

| Quy tắc | Chi tiết |
|---|---|
| Không giữ trạng thái và an toàn luồng | Mỗi lớp có một thể hiện được lưu đệm và dùng chung cho mọi lần phân tích. |
| Hàm khởi tạo công khai không tham số | Chỉ cần khi bộ sửa đổi được gọi theo tên lớp. |
| Xử lý đầu vào `null` và rỗng | Bộ phân tích không lọc chúng ra trước khi chuỗi chạy. |
| Trả về `null` nghĩa là "không thay đổi" | Giá trị trước đó được chuyển tiếp. Hãy trả về `input` nguyên vẹn khi bộ sửa đổi không áp dụng. |
| Nên trả về nguyên vẹn hơn là ném ngoại lệ | Một bộ sửa đổi ném ngoại lệ sẽ hủy việc phân tích — xem [Xử lý thất bại](#xử-lý-thất-bại-của-bộ-sửa-đổi). |
| `getName()` | Ghi đè để kiểm soát tên được báo cáo trên `ModifierInfo`; mặc định là tên lớp giản lược. |

### Đăng ký bộ sửa đổi

Các bộ sửa đổi chạy theo đúng thứ tự được thêm vào, mỗi bộ nhận kết quả của bộ trước. Hãy đăng ký chúng bằng thể hiện, bằng tên lớp đầy đủ, hoặc bằng một danh sách gồm một trong hai:

```java
ParseConfig config = ParseConfig.builder()
        .modifier(new GsPlaceholderModifier())                          // by instance
        .modifierClass("com.example.gaia.StripVendorWrapperModifier")   // by class name
        .build();

// Or from external configuration — a list of fully-qualified class names, in execution order
ParseConfig fromConfig = ParseConfig.builder()
        .modifierClasses(List.of("tools.pantheum.gaia.modifier.custom.ModifierRemoveSpaces",
                                 "tools.pantheum.gaia.modifier.custom.ModifierRemoveAIBrackets",
                                 "com.example.gaia.StripVendorWrapperModifier"))
        .build();

ParseResult result = parser.parse("SCAN:10LOT-A{GS}17271231", config);
```

[Các bộ sửa đổi có sẵn](#các-bộ-sửa-đổi-có-sẵn) cũng được đặt tên như bộ của bạn — **luôn luôn bằng tên đầy đủ**. Không có tên rút gọn hay tra cứu bí danh nào cho chúng; `ModifierRegistry` phân giải mọi bộ sửa đổi, dù đi kèm hay không, bằng tên lớp đầy đủ.

Tên được `ModifierRegistry` phân giải; nó tạo một thể hiện của mỗi lớp qua hàm khởi tạo không tham số, đúng một lần, rồi lưu đệm thể hiện ấy cho mọi cấu hình về sau có nhắc tới cùng lớp đó. Việc phân giải xảy ra **khi cấu hình được dựng**, nên một cái tên không tìm thấy, không hiện thực `ModifierInterface`, hoặc không tạo được thể hiện, sẽ ném `IllegalArgumentException` ngay tại đó — chứ không lặng lẽ vào lúc phân tích. Một bộ sửa đổi không thể dựng bằng phản chiếu (chẳng hạn bộ có mang một phụ thuộc được tiêm vào) có thể đăng ký trước để vẫn gọi được theo tên:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Xem bộ sửa đổi đã làm gì

Khi có bộ sửa đổi được cấu hình, `ParseResult.getPayload()` phản ánh đầu vào **đã sửa đổi**. Bản gốc được giữ lại trên `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` báo cáo `getName()` của từng bộ sửa đổi, vốn mặc định là tên lớp giản lược nhưng cả hai bộ có sẵn đều ghi đè nó — nên một chuỗi gồm hai bộ ấy sẽ báo cáo tên hiển thị chứ không phải tên lớp:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` trả về `null` khi không có bộ sửa đổi nào được cấu hình. Khi các bộ sửa đổi có chạy nhưng bộ nào cũng trả về đầu vào nguyên vẹn, thông tin vẫn hiện diện và `isModified()` là `false` — chỉ những bộ thực sự làm thay đổi đầu vào mới được liệt kê trong `getAppliedModifiers()`.

### Xử lý thất bại của bộ sửa đổi

Một bộ sửa đổi ném ngoại lệ sẽ hủy việc phân tích. Ngoại lệ ấy được bọc trong một `GaiaModifierException` có nêu tên bộ sửa đổi gây lỗi, và kết quả mang một lỗi nội bộ `GE-I001` với thông báo chứa chính cái tên đó; `getPayload()` báo cáo đầu vào chưa sửa đổi. Việc phân tích cố ý **không** tiếp tục với một chuỗi mới viết lại được nửa chừng — một bước chuẩn hóa thất bại trong im lặng sẽ tạo ra những kết quả trông có vẻ hợp lệ nhưng lại được phân tích từ đầu vào sai.

---

## Chế độ phân tích

Mỗi chế độ được đặt tên theo [giai đoạn](#chuỗi-xử-lý-phân-tích) sâu nhất mà nó chạy; mọi giai đoạn trước đó vẫn chạy.

| Chế độ | Chạy tới | Trả lời câu hỏi |
|---|---|---|
| `DATA_CARRIER` | Giai đoạn 1 (định tuyến đầu vào) | Hệ ký hiệu nào đã mang cái này? |
| `SYNTAX` | Giai đoạn 2 (cú pháp) | Mã AI và độ dài có đúng dạng không? |
| `CONTENT` | Giai đoạn 3 (nội dung) | Các giá trị có phải dữ liệu GS1 hợp lệ không? |
| `INTERPRETATION` | Giai đoạn 4 (diễn giải) | Các giá trị có nghĩa gì? |

### Chế độ DATA_CARRIER

Dừng sau Giai đoạn 1 — kiểm tra hợp lệ Mã nhận dạng AIM và xác định hệ ký hiệu, nhưng không bước vào chuỗi xử lý phân tích AI. Hữu ích để nhận diện hệ ký hiệu và định tuyến mà không phải chịu phí tổn của việc kiểm tra hợp lệ đầy đủ.

```java
// GS1-128 prefixed input (]C1 = GS1-128 / ISBT 128)
ParseResult response = parser.parse(
    "]C10109506000134352",
    ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

System.out.println(response.hasDataCarrier());       // true
System.out.println(response.getDataCarrier().getName());
// → GS1-128 / ISBT 128
System.out.println(response.getDataCarrier().getAimCodeId());
// → ]C1
System.out.println(response.getDataCarrier().isGs1AICapable());
// → true
System.out.println(response.getDataCarrier().getDataCarrierType());
// → GS1_128   (typed symbology — see DataCarrierEntry and DataCarrierType)
System.out.println(response.getAiObject());          // null — AI parsing not performed

// Unrecognised AIM Code ID
ParseResult unknown = parser.parse("]Z9somedata",
    ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());
System.out.println(unknown.isValid());               // false
unknown.getErrors().forEach(e ->
    System.out.println("[" + e.getId() + "] " + e.getMessage()));
// → [GE-D001] AIM Code ID ']Z9' is not a recognised data carrier
```

**Dùng khi:** ứng dụng của bạn cần biết loại mã vạch trước khi quyết định xử lý phần tải ra sao — chẳng hạn để định tuyến hệ ký hiệu 1D và 2D tới những bộ xử lý khác nhau. Với việc định tuyến ấy, hãy dùng [`DataCarrierType`](#datacarrierentry-và-datacarriertype) có kiểu (`getDataCarrier().getDataCarrierType()`) thay vì so khớp chuỗi trên `getName()`.

---

### Chế độ SYNTAX

Dừng sau Giai đoạn 2. Hữu ích để sàng lọc cấu trúc từ sớm mà không tốn chi phí kiểm tra nội dung.

```java
ParseResult response = parser.parse(
    "0109506000134352",
    ParseConfig.builder().requestedParseMode(ParseMode.SYNTAX).build());

// Tells you: is the AI structure valid?
// Does NOT tell you: is the GTIN check digit correct?
System.out.println(response.isValid()); // true — syntax is fine

for (GS1AIObjectElement e : response.getAiObject().getAis()) {
    System.out.println("(" + e.getAi() + ") " + e.getTitle() + " = " + e.getValue());
}
// → (01) GTIN = 09506000134352
```

**Dùng khi:** bạn muốn kiểm tra rằng mã AI và độ dài dữ liệu đúng dạng trước khi bước vào việc kiểm tra hợp lệ đầy đủ, hoặc khi quét khối lượng lớn mà lỗi nội dung hiếm khi xảy ra.

---

### Chế độ CONTENT

Dừng sau Giai đoạn 3.

```java
// Valid input
ParseResult response = parser.parse("01095060001343521726123110LOT-001",
    ParseConfig.builder().requestedParseMode(ParseMode.CONTENT).build());

System.out.println(response.isValid());              // true
System.out.println(response.getAiObject().toHriString());
// → (01)09506000134352 (17)261231 (10)LOT-001

// Invalid GTIN check digit
ParseResult bad = parser.parse("0109506000134351",
    ParseConfig.builder().requestedParseMode(ParseMode.CONTENT).build());

System.out.println(bad.isValid());                   // false
bad.getErrors().forEach(e ->
    System.out.println("[" + e.getId() + "] " + e.getMessage()));
// → [GE-C003] Check digit validation failed for AI (01) value '09506000134351'

// Variable-length AI followed by another AI — GS separator required
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult multi = parser.parse(input,
    ParseConfig.builder().requestedParseMode(ParseMode.CONTENT).build());

System.out.println(multi.isValid());                 // true
multi.getAiObject().getAis().forEach(e ->
    System.out.printf("(%s) %s%n", e.getAi(), e.getValue()));
// → (01) 09506000134352
// → (10) LOT-ABC
// → (21) SN-98765
```

> Phần lớn AI không thể đứng một mình: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) và
> `21` (SERIAL) — mỗi cái đều *cần* một khóa nhận dạng như AI `01` trong cùng chuỗi phần
> tử, nên nếu bỏ GTIN ở ví dụ trên thì việc phân tích sẽ hỏng ở Giai đoạn 2 với `GE-S005`
> chứ không hề đi tới bước kiểm tra nội dung. Hãy đặt `skipRequiresCheck(true)` trên
> `ParseConfig` để phân tích những mảnh cố ý lược bỏ AI đi kèm của chúng.

**Dùng khi:** bạn cần biết một giá trị vừa quét có tuân thủ GS1 hoàn toàn hay không trước khi dùng nó trong một quy trình nghiệp vụ, mà không muốn chịu phí tổn của việc làm giàu diễn giải.

---

### Chế độ INTERPRETATION (mặc định)

Chạy trọn chuỗi xử lý tới Giai đoạn 4. Đây là mặc định khi gọi `parse(String)` không kèm tham số chế độ. Chỉ làm giàu những phần tử đã vượt qua bước kiểm tra nội dung một cách sạch sẽ.

```java
// GTIN with expiry date and batch/lot
String input = "0109506000134352" + "17261231" + "10LOT-001";
ParseResult response = parser.parse(input,
    ParseConfig.builder().requestedParseMode(ParseMode.INTERPRETATION).build());

System.out.println(response.isValid());              // true
System.out.println(response.getAiObject().toHriString());
// → (01)09506000134352 (17)261231 (10)LOT-001

for (GS1AIObjectElement element : response.getAiObject().getAis()) {
    System.out.println("AI " + element.getAi() + " — " + element.getTitle());
    for (GS1AIInterpretation interp : element.getInterpretations()) {
        System.out.printf("  %-25s : %s%n", interp.getLabel(), interp.getValue());
    }
}
```

**Kết quả ví dụ:**
```
AI 01 — GTIN
  GS1 member code            : 950
  GS1 member organisation    : GS1 Global Office
  GTIN type                  : GTIN-13
  GTIN                       : 9506000134352
  Check digit                : 2

AI 17 — USE BY or EXPIRY
  Date                       : 31/12/2026
  Date format                : dd/mm/yyyy

AI 10 — BATCH/LOT
  (no interpretations — a free-text lot number carries no derivable metadata)
```

**Ví dụ về số tiền (AI 3932 — giá kèm mã tiền tệ ISO):**
```java
// AI 3932 requires a variable-measure AI in the same element string — here AI 3103.
ParseResult price = parser.parse("]d2" + "0109506000134352" + "3103001500" + "3932036002953",
    ParseConfig.builder().requestedParseMode(ParseMode.INTERPRETATION).build());

GS1AIObjectElement ai = price.getAiObject().get("3932");
ai.getInterpretations().forEach(i ->
    System.out.printf("%-28s : %s%n", i.getLabel(), i.getValue()));
// Currency code                : 036
// Currency alpha code          : AUD
// Currency name                : Australian Dollar
// Amount                       : 29.53
// Decimal places               : 2
// Monetary amount              : AUD 29.53
// Monetary amount (formatted)  : A$29.53
```

**Dùng khi:** xây dựng lớp hiển thị, công cụ kiểm tra nhãn, hay bất kỳ giao diện nào cần phân rã giá trị AI theo cách thân thiện với con người.

---

## Mã tương quan

Một số quy trình đặt thêm một mã tương quan 8 chữ số riêng của mình vào trước đầu vào GS1 thô, để có thể gắn các sự kiện quét trở lại với một phiên làm việc hay một giao dịch. Định dạng của nó là:

```
DDDDDDDD~<GS1 content>

Example:
  12345678~0109506000134352
  ^^^^^^^^
  8-digit correlation ID
          ^
          tilde separator
           ^^^^^^^^^^^^^^^^
           GS1 element string (passed to the normal pipeline)
```

Dấu `~` (dấu ngã) là dấu phân tách. Nó **không** thuộc nội dung GS1 — nó được tách bỏ trước khi bất kỳ việc phân tích GS1 nào bắt đầu.

### Quy tắc nhận diện

Tiền tố được nhận diện khi đầu vào bắt đầu bằng đúng 8 chữ số thập phân ASCII (`0`–`9`) và ngay sau đó là `~`. Nếu ký tự thứ 9 không phải `~`, hoặc một trong 8 ký tự đầu không phải chữ số, thì đầu vào được coi là nội dung GS1 thuần túy không có tiền tố tương quan.

### Truy cập mã tương quan

```java
ParseResult response = parser.parse("12345678~0109506000134352");

System.out.println(response.hasCorrelationId());           // true
System.out.println(response.getCorrelationInfo().getId()); // "12345678"
System.out.println(response.getPayload());                 // "0109506000134352"

// Without a prefix — hasCorrelationId() is false
ParseResult plain = parser.parse("0109506000134352");
System.out.println(plain.hasCorrelationId());              // false
System.out.println(plain.getCorrelationInfo());            // null
```

### Kết hợp với Mã nhận dạng AIM

Tiền tố tương quan có thể xuất hiện trước một Mã nhận dạng AIM. Bộ phân tích xử lý điều này một cách trong suốt:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Lớp hiện thực:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Một **GS1 Digital Link** mã hóa một hoặc nhiều giá trị AI ngay trong cấu trúc của một URL HTTP(S), nhờ đó sản phẩm vật lý có được mã nhận dạng phân giải được qua web. GAIA hiện thực *GS1 Digital Link Standard: URI Syntax* (bản phát hành 1.7.0) cho URI **không nén**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` tự nhận ra URI Digital Link — mọi đầu vào bắt đầu bằng `http://` hoặc `https://` đều được chuyển tới `GS1DLParser`, nơi chạy đúng những giai đoạn nội dung và diễn giải như chuỗi xử lý dành cho chuỗi phần tử.

### Cấu trúc URI và vai trò của AI

Mỗi AI trong một URI Digital Link đảm nhận một trong ba vai trò, được cung cấp trên mỗi `GS1AIObjectElement` qua `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Vai trò | Vị trí | Ví dụ |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Cặp `/ai/value` đầu tiên trên đường dẫn (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Các cặp đường dẫn tiếp theo, sắp thứ tự theo khóa chính (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Tham số truy vấn có khóa hoàn toàn bằng chữ số (§4.10) | `?17=271231` |

Các quy tắc cấu trúc được thực thi (`DLPathRules`):
- Đúng **một** khóa nhận dạng chính trên đường dẫn; các khóa thêm phải được mã hóa thành thuộc tính dữ liệu trong truy vấn.
- Bổ ngữ khóa phải được khóa chính chấp nhận và phải xuất hiện đúng thứ tự quy định. Bổ ngữ tùy chọn có thể lược bỏ, nhưng những bổ ngữ *có mặt* vẫn phải theo đúng thứ tự cố định — xem [Thứ tự bổ ngữ](#thứ-tự-bổ-ngữ).
- Trước khóa chính có thể có những đoạn đường dẫn tùy ý (ví dụ `/products/au/01/...`); hãy lấy chúng qua `getDigitalLinkInfo().getCustomPathStem()`.
- Các khóa truy vấn không phải chữ số (`linkType`, `context`, tham số mở rộng như `23P`) bị bỏ qua; khóa hoàn toàn bằng chữ số phải là AI hợp lệ có gắn cờ `validAsDataAttribute`.
- Ký tự giá trị mã hóa phần trăm được giải mã; AI `(03)` và `(8014)` không được phép.

Các khóa chính và những chuỗi bổ ngữ mà chúng chấp nhận được **lấy từ dữ liệu** trong các định nghĩa AI — từ cờ `gs1DigitalLinkPrimaryKey` và thuộc tính `gs1DigitalLinkQualifiers` — chứ không viết cứng trong mã.

Mọi vi phạm cấu trúc, hoặc một đầu vào không phải URL, đều sinh ra một lỗi cấu trúc Digital Link (`GE-L001`–`GE-L014`, mỗi điều kiện một mã). Siêu dữ liệu URL đã phân rã (`scheme`, `domain`, `path`, `customPathStem`, `query`, và `java.net.URL`) vẫn lấy được qua `getDigitalLinkInfo()` ngay cả khi có lỗi cấu trúc.

### Thứ tự bổ ngữ

Với mỗi khóa chính, `gs1DigitalLinkQualifiers` liệt kê một hoặc nhiều chuỗi bổ ngữ **có thứ tự**. Trong một chuỗi, AI đặt trong ngoặc vuông là **tùy chọn**, AI không có ngoặc là **bắt buộc** — đúng như ký pháp `[cpv-comp]` của phần ABNF ở §4.9. Các chuỗi của cùng một khóa chính là những phương án loại trừ lẫn nhau.

Chẳng hạn GTIN (`01`) định nghĩa hai chuỗi:

| Đường dẫn | Chuỗi | Ý nghĩa |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — mỗi cái đều tùy chọn, nhưng cố định theo thứ tự này |
| upui-path | `235` | TPX (bắt buộc); GTIN + TPX = UPUI |

Vậy nên `/01/09506000134352/10/LOT-ABC/21/SER` là hợp lệ (LOT trước SER, CPV được lược bỏ), `/01/.../21/SER/10/LOT-ABC` bị **từ chối** (sai thứ tự), còn `/01/09506000134352/235/2ABC456` là upui-path. Phép kiểm tra thứ tự là một phép khớp dãy con giữ nguyên thứ tự, nên các AI tùy chọn có thể bỏ qua nhưng không bao giờ được đảo thứ tự.

```java
ParseResult resp = parser.parse(
    "https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

resp.getAiObject().hasDigitalLink();        // true
resp.getContentType();                       // GS1_DIGITAL_LINK
resp.getAiObject().get("01").getDigitalLinkAIType();  // PRIMARY_IDENTIFICATION_KEY
resp.getAiObject().get("17").getDigitalLinkAIType();  // DATA_ATTRIBUTE

// Canonical form on id.gs1.org (data attributes become query parameters)
resp.getAiObject().getCanonicalDigitalLink();
//   https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231

// Any custom path stem before the primary key (empty here)
resp.getAiObject().getDigitalLinkInfo().getCustomPathStem();  // ""

// Convert to the equivalent raw element string (FNC1-separated)
resp.getAiObject().toElementString();       // 010950600013435210LOT-ABC<GS>17271231
```

**Lớp hiện thực:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Làm việc với kết quả

### ParseResult

Kết quả ở cấp cao nhất do `GaiaParser.parse()` trả về.

| Phương thức | Trả về | Mô tả |
|---|---|---|
| `isValid()` | `boolean` | `true` nếu không có lỗi ở bất kỳ cấp nào. Cảnh báo không ảnh hưởng tới tính hợp lệ. Luôn là `true` khi `getAiObject()` là `null`. |
| `getPayload()` | `String` | Chuỗi đầu vào sau khi tách tiền tố tương quan — và sau khi mọi [bộ sửa đổi đầu vào](#bộ-sửa-đổi-đầu-vào) đã viết lại nó. |
| `getPayloadContent()` | `String` | Phần tải sau khi tách bỏ Mã nhận dạng AIM và tiền tố ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (vật mang dữ liệu bị từ chối vì không phải GS1, ví dụ vật mang `]A0` của Code 39), hoặc `UNABLE_TO_DETERMINE_CONTENT` (khi `aiObject` là `null`, ví dụ ở chế độ `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Độ sâu chuỗi xử lý đã cấu hình (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Giai đoạn sâu nhất mà việc phân tích thực sự đạt tới — xem bên dưới. |
| `isParseComplete()` | `boolean` | `true` nếu việc phân tích đạt tới độ sâu đã yêu cầu (`achieved == requested`). Độc lập với `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Mọi AI đã giải mã. Là `null` ở chế độ `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Mọi lỗi không phải WARNING (cấp đối tượng + toàn bộ cấp phần tử). |
| `getWarnings()` | `List<GaiaError>` | Mọi khuyến cáo WARNING (cấp đối tượng + toàn bộ cấp phần tử). |
| `hasWarnings()` | `boolean` | `true` nếu có bất kỳ khuyến cáo WARNING nào được nêu. |
| `getIssues()` | `List<GaiaError>` | Lỗi và cảnh báo gộp lại. |
| `hasDataCarrier()` | `boolean` | `true` nếu một Mã nhận dạng AIM đã được nhận ra. |
| `getDataCarrier()` | `DataCarrierEntry` | Siêu dữ liệu về hệ ký hiệu, hoặc `null` nếu không xác định được vật mang. |
| `hasEci()` | `boolean` | `true` nếu một chỉ báo ECI đã được tách khỏi phần tải. |
| `getEci()` | `EciEntry` | Siêu dữ liệu mã hóa ECI, hoặc `null`. |
| `hasCorrelationId()` | `boolean` | `true` nếu đầu vào gốc có tiền tố tương quan `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | Mã tương quan đã trích, hoặc `null` nếu không có. |
| `isInputModified()` | `boolean` | `true` nếu một [bộ sửa đổi đầu vào](#bộ-sửa-đổi-đầu-vào) đã làm thay đổi đầu vào. |
| `getModifierInfo()` | `ModifierInfo` | Chuỗi bộ sửa đổi đã làm gì — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. Là `null` nếu không có bộ sửa đổi nào được cấu hình. |
| `getTiming()` | `ProcessingTiming` | Thời gian thực của lần phân tích — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. Là `null` nếu không do `GaiaParser` tạo ra. |
| `getVersion()` | `String` | Phiên bản thư viện đã tạo ra kết quả này. |

#### Chế độ phân tích được yêu cầu và chế độ đạt được

Chuỗi xử lý leo theo bậc thang **SYNTAX → CONTENT → INTERPRETATION** và dừng sớm khi gặp lỗi, nên chế độ thực sự *đạt được* có thể nông hơn chế độ được *yêu cầu*. `getAchievedParseMode()` cho biết nó đã đi tới đâu:

| Yêu cầu | Điều gì xảy ra | Đạt được | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | một lỗi **cú pháp / cấu trúc** làm việc phân tích dừng sau bước tách token | `SYNTAX` | `false` |
| `INTERPRETATION` | một lỗi **nội dung** (sai định dạng/chữ số kiểm tra) chặn việc làm giàu | `CONTENT` | `false` |
| `CONTENT` | giai đoạn nội dung luôn chạy tới hết (lỗi được ghi chú, không gây dừng) | `CONTENT` | `true` |
| bất kỳ (đầu vào sạch) | chuỗi xử lý đạt tới độ sâu được yêu cầu | = yêu cầu | `true` |
| `DATA_CARRIER` | vật mang được kiểm tra hợp lệ; nội dung AI không được phân tích | `DATA_CARRIER` | `true` |
| bất kỳ | vật mang dữ liệu bị từ chối trước khi phân tích AI (ví dụ vật mang `]A0` không thuộc GS1) | `SYNTAX` | `false` |

`isParseComplete()` độc lập với `isValid()`: một lần phân tích `CONTENT` trên GTIN có chữ số kiểm tra sai là **hoàn tất** (giai đoạn nội dung đã chạy) nhưng **không hợp lệ** (chữ số kiểm tra hỏng). Hãy dùng `isParseComplete()` để hỏi "chuỗi xử lý có chạy sâu như tôi yêu cầu không?" và `isValid()` để hỏi "dữ liệu có đúng dạng không?".

```java
ParseResult r = parser.parse("0109506000134350",          // bad check digit
        ParseConfig.builder().requestedParseMode(ParseMode.INTERPRETATION).build());
r.getRequestedParseMode();  // INTERPRETATION
r.getAchievedParseMode();   // CONTENT  — enrichment was skipped because of the content error
r.isParseComplete();        // false
r.isValid();                // false
```

---

### GS1AIObject

Tập hợp các phần tử AI đã giải mã.

| Phương thức | Mô tả |
|---|---|
| `getAis()` | Mọi thể hiện `GS1AIObjectElement` theo thứ tự trong đầu vào. |
| `get(String aiCode)` | Phần tử đầu tiên khớp mã AI đã cho, hoặc `null`. |
| `contains(String aiCode)` | `true` nếu có AI mang mã đó. |
| `size()` | Số AI đã giải mã. |
| `isValid()` | `true` nếu không có lỗi cấp đối tượng và không phần tử nào có lỗi. |
| `toHriString()` | Chuỗi HRI, ví dụ `(01)09506000134352 (17)261231`. |
| `toElementString()` | Chuỗi phần tử thô — không dấu ngoặc, có FNC1 sau mỗi phần tử độ dài thay đổi — ví dụ `010950600013435210LOT-ABC<GS>17271231`. Trả về `null` nếu `isValid()` là `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` khi `hasDigitalLink()` là đúng, ngược lại `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` nếu đầu vào là một URI GS1 Digital Link có mang khóa nhận dạng chính. Một URL đúng dạng nhưng không có khóa chính vẫn cung cấp `getDigitalLinkInfo()` nhưng ở đây trả về `false`. |
| `getCanonicalDigitalLink()` | URI GS1 Digital Link chuẩn tắc (§4.12) trên `https://id.gs1.org` — khóa chính và các bổ ngữ là những đoạn đường dẫn, thuộc tính dữ liệu là các tham số truy vấn sắp theo khóa AI — hoặc `null` nếu không có khóa chính. |
| `getDigitalLinkInfo()` | Siêu dữ liệu phân rã URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), hoặc `null` nếu không phải Digital Link. |
| `getAllErrors()` | Lỗi cấp đối tượng + mọi lỗi của phần tử (không phải WARNING). |
| `getAllWarnings()` | Cảnh báo cấp đối tượng + mọi cảnh báo của phần tử. |
| `getAllIssues()` | Tất cả gộp lại. |

---

### GS1AIObjectElement

Một thể hiện AI đã giải mã.

| Phương thức | Mô tả |
|---|---|
| `getAi()` | Mã AI, ví dụ `"01"`, `"3102"`. |
| `getTitle()` | Tiêu đề dữ liệu GS1, ví dụ `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Mô tả GS1 đầy đủ của AI, **đã bản địa hóa sang ngôn ngữ phân tích** (trong tiếng Anh là `"Global Trade Item Number (GTIN)"`). Quay về văn bản tiếng Anh trong định nghĩa AI nếu chưa được dịch. |
| `getFormatString()` | Mô tả định dạng bao gồm cả AI *lẫn* dữ liệu của nó, ví dụ `"N2+N14"` cho AI `01`, `"N2+X..20"` cho AI `10`, `"N4+N3+N..15"` cho AI `3932`. |
| `getValue()` | Giá trị dữ liệu thô trích từ chuỗi phần tử. |
| `isFixedLength()` | `true` nếu AI có độ dài dữ liệu cố định. |
| `getPosition()` | Vị trí ký tự tính từ 0 trong đầu vào gốc. |
| `getGS1ComponentValues()` | Các lát giá trị theo từng thành phần (với AI nhiều thành phần). |
| `getErrors()` | Lỗi cấp phần tử không phải WARNING. |
| `getWarnings()` | Khuyến cáo WARNING ở cấp phần tử. |
| `getIssues()` | Lỗi và cảnh báo ở cấp phần tử, gộp lại. |
| `hasErrors()` | `true` nếu có bất kỳ lỗi không phải WARNING nào được gắn kèm. |
| `hasWarnings()` | `true` nếu có bất kỳ khuyến cáo WARNING nào được gắn kèm. |
| `getInterpretations()` | Các mục `GS1AIInterpretation` (được điền ở chế độ INTERPRETATION). |
| `getInterpretation(String type)` | Diễn giải đầu tiên khớp khóa kiểu `GS1Constants_Enricher` đã cho, hoặc `null`. |
| `getDigitalLinkAIType()` | Vai trò Digital Link của phần tử (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), hoặc `null` với đầu vào là chuỗi phần tử. |
| `hasDigitalLinkAIType()` | `true` nếu một vai trò Digital Link đã được gán. |

---

### GaiaError

Một lỗi kiểm tra hợp lệ hoặc khuyến cáo, bất biến.

| Phương thức | Mô tả |
|---|---|
| `getId()` | Mã định danh trong danh mục, ví dụ `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, hoặc `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, hoặc `INTERNAL`. |
| `getCode()` | Mã ngắn mà máy đọc được. |
| `getAi()` | Mã AI đã gây ra lỗi, hoặc `null` với lỗi cấp đối tượng. |
| `getMessage()` | Thông báo mà con người đọc được, đã điền giá trị vào. |
| `getPosition()` | Vị trí ký tự tính từ 0 trong đầu vào gốc. |

---

### GS1AIInterpretation

Một mảnh diễn giải có nhãn, được gắn vào một `GS1AIObjectElement` ở chế độ `INTERPRETATION`.

| Phương thức | Mô tả |
|---|---|
| `getType()` | Khóa kiểu mà máy đọc được, ví dụ `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Không đổi giữa các ngôn ngữ. |
| `getLabel()` | Nhãn mà con người đọc được, **đã bản địa hóa sang ngôn ngữ phân tích** (trong tiếng Anh là `"Date"` / `"GS1 company prefix"`). |
| `getValue()` | Giá trị đã trích/làm giàu, ví dụ `"31/12/2026"`, `"9506000"`. Không bản địa hóa. |

---

### DataCarrierEntry và DataCarrierType

Khi đầu vào mang một Mã nhận dạng AIM, `ParseResult.getDataCarrier()` trả về một `DataCarrierEntry` mô tả ký hiệu đã chuyên chở dữ liệu. Mục này là bản ghi cụ thể trong sổ đăng ký ứng với Mã nhận dạng AIM đã khớp; còn `DataCarrierType` là enum ở thời điểm biên dịch mà nó thuộc về.

#### DataCarrierEntry

Siêu dữ liệu của một Mã nhận dạng AIM đã được nhận ra (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Phương thức | Mô tả |
|---|---|
| `getAimCodeId()` | Mã nhận dạng AIM đã khớp, ví dụ `"]C1"`. |
| `getName()` | Tên mà con người đọc được của ký hiệu cụ thể, ví dụ `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Mô tả dài hơn về vật mang. |
| `getType()` | Kiểu cấu trúc của vật mang dưới dạng chuỗi (phản ánh `getDataCarrierType().getCategory()`). |
| `getStandard()` | Tiêu chuẩn của hệ ký hiệu, nếu có ghi nhận. |
| `getDataCarrierType()` | `DataCarrierType` có kiểu ứng với mục này — hãy ưu tiên dùng nó để định tuyến trong mã. |
| `isGs1Capable()` | `true` nếu vật mang có thể chứa dữ liệu GS1 (chuỗi phần tử AI và/hoặc Digital Link). |
| `isGs1AICapable()` | `true` nếu vật mang có thể chứa chuỗi phần tử AI GS1. |
| `isGs1DigitalLinkCapable()` | `true` nếu vật mang có thể chứa một URI GS1 Digital Link. |
| `isEciCapable()` | `true` nếu vật mang hỗ trợ chỉ báo ECI. |
| `isRequiresGtinPadding()` | `true` với các vật mang EAN/UPC/ITF có giá trị số được đệm lên GTIN-14 trước khi phân tích AI. |

#### DataCarrierType

Một enum ở thời điểm biên dịch gồm các kiểu vật mang dữ liệu, khóa theo Mã nhận dạng AIM được cấp phát trong ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Ký tự sau `]` (*ký tự mã*) chọn ra họ; phần lớn các họ ánh xạ về một hằng số duy nhất bao trùm mọi bộ bổ trợ (`ITF` bao gồm `]I0`–`]I2`; `EAN_UPC` bao gồm EAN-13, UPC-A, UPC-E và EAN-8). Ở đâu GS1 dành riêng một bộ bổ trợ cho dữ liệu AI, biến thể ấy có hằng số riêng — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — tách khỏi các bản thường tương ứng. Khi không có Mã nhận dạng AIM, hoặc khi mã ấy trỏ tới một vật mang không rõ, kiểu sẽ là `UNKNOWN`.

| Phương thức | Mô tả |
|---|---|
| `getCategory()` | Nhóm rộng `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, hoặc `OTHER`. |
| `getCodeChar()` | Ký tự mã AIM xác định họ, ví dụ `"Q"` cho QR Code; `null` với `UNKNOWN`. |
| `getDisplayName()` | Tên mà con người đọc được của *kiểu* (có thể rộng hơn `DataCarrierEntry.getName()` — ví dụ `"EAN-13 / UPC-A / UPC-E / EAN-8"` so với `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` với những hằng số luôn biểu thị dữ liệu AI GS1: bốn biến thể GS1 dành riêng (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) cùng với `GS1_DATABAR`, vốn tự thân là GS1 vì mọi bộ bổ trợ `]e` đều là GS1 DataBar. Hẹp hơn `DataCarrierEntry.isGs1AICapable()` — một `QR_CODE` thường vẫn có thể mang dữ liệu AI GS1. |
| `static forAimCodeId(String)` | Phân giải kiểu trực tiếp từ một Mã nhận dạng AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); trả về `UNKNOWN` với mã vắng mặt, sai dạng hoặc không nhận ra. |

Định tuyến theo kiểu thay vì theo tên — ví dụ tách ký hiệu tuyến tính (Code-128) khỏi ký hiệu 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` chỉ bao gồm các ký hiệu ma trận và ký hiệu chấm; những vật mang tuyến tính xếp chồng
(`PDF417`, `CODE_16K`, `CODABLOCK`, `CODE_49`) thuộc `STACKED_LINEAR`, dù chúng vẫn thường
được gọi là mã vạch "2D". Muốn coi cả hai là một nhóm — chẳng hạn để quyết định xem có cần
máy quét ảnh thay cho máy quét laser hay không — hãy kiểm tra cả hai nhóm.

> Việc phân giải kiểu cần có Mã nhận dạng AIM trong lần quét; thiếu nó thì `getDataCarrier()` là `null` và kiểu là `UNKNOWN`. Hãy cấu hình máy quét để truyền kèm tiền tố Mã nhận dạng AIM.

---

## Tham chiếu lỗi

| Mã | Mức | Giai đoạn | Ý nghĩa |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Tiền tố AI không rõ — không xác định được độ dài dữ liệu |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Đầu vào quá ngắn để đọc trọn một mã AI |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Giá trị bị cắt cụt — ít ký tự hơn mức AI đòi hỏi |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Mã nhận dạng ứng dụng trùng lặp trong chuỗi phần tử |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Thiếu phụ thuộc AI bắt buộc |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Cặp AI bị loại trừ — hai AI không thể cùng xuất hiện |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Thất bại ngoài dự kiến khi tách token |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Ký tự ngoài tập ký tự mà GS1 mã hóa được, trong chuỗi phần tử |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Thiếu dấu phân tách FNC1 bắt buộc sau một AI độ dài thay đổi |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Dữ liệu thừa vượt quá mọi giới hạn tối đa của các thành phần |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Dấu phân tách FNC1 sau một AI độ dài cố định ở giữa chuỗi |
| `GE-W002` | WARNING | SYNTAX | FNC1 thừa ở cuối chuỗi phần tử (chỉ là khuyến cáo) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Vi phạm cấu trúc URI Digital Link — mỗi điều kiện một mã (URI sai dạng, lược đồ, máy chủ, thứ tự bổ ngữ, AI bị cấm, không có khóa chính (`GE-L013`), nhiều khóa chính (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Giá trị không khớp mẫu biểu thức chính quy của AI |
| `GE-C003` | DATA_ERROR | CONTENT | Kiểm tra chữ số kiểm tra thất bại |
| `GE-C004` | DATA_ERROR | CONTENT | Kiểm tra cặp ký tự kiểm tra thất bại |
| `GE-C005` | FORMAT_ERROR | CONTENT | Giá trị thành phần chứa ký tự ngoài tập ký tự cho phép |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Thất bại về định dạng thành phần — mỗi điều kiện kiểm tra một mã (xem `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Thất bại khi kiểm tra ngữ nghĩa riêng — mỗi điều kiện kiểm tra một mã (xem `content/validator/`). **Ngoại lệ:** 14 phép kiểm tra tiền tố công ty GS1 liệt kê dưới đây mang mức `WARNING`, và `GE-C168` (mã quốc gia dạng số ISO 3166-1 không nhận ra được) mang mức `FORMAT_ERROR`. |
| Kiểm tra tiền tố công ty GS1 | WARNING | CONTENT | Khóa không bắt đầu bằng một tiền tố công ty GS1 nhận ra được, trên các AI mang khóa GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Chỉ là khuyến cáo — không ảnh hưởng tính hợp lệ. |
| `GE-C169` | DATA_ERROR | CONTENT | Chữ số kiểm tra IMEI (Luhn) thất bại trên AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Chữ số kiểm tra EID (Luhn) thất bại trên AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Mã nhận dạng AIM không nhận ra được |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Đã xác định được vật mang nhưng nó không hỗ trợ cả chuỗi phần tử AI GS1 lẫn URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Lỗi nội bộ ngoài dự kiến |

> **Một khiếm khuyết đã biết trong việc dựng thông báo.** Các mẫu trong danh mục đặt giá trị
> chèn vào giữa dấu nháy đơn đôi theo kiểu MessageFormat (`''{value}''`), nhưng
> `ErrorRegistry` lại chèn bằng `String.replace` thuần túy, nên phần nháy đôi ấy còn nguyên
> tới tận `getMessage()` — hiện tại bạn sẽ thấy `value ''09506000134351''` ở chỗ mà các
> thông báo được trích trong hướng dẫn này hiển thị `value '09506000134351'`. Điều này ảnh
> hưởng tới mọi thông báo có trích giá trị trong cả 35 danh mục ngôn ngữ. Đừng phân tích
> thông báo lỗi; hãy so khớp theo `getId()` / `getCode()`.

---

## An toàn luồng

`GaiaParser` an toàn luồng ngay khi đã được khởi tạo. Một thể hiện duy nhất có thể dùng chung giữa nhiều luồng và gọi đồng thời. Cách làm được khuyến nghị là tạo một thể hiện lúc ứng dụng khởi động rồi dùng lại nó:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` là bất biến và cũng an toàn để dùng chung như vậy. Nghĩa vụ an toàn luồng duy nhất mà thư viện không thể thực thi thay bạn nằm ở [bộ sửa đổi đầu vào](#bộ-sửa-đổi-đầu-vào): mỗi bộ sửa đổi chỉ có một thể hiện được lưu đệm và dùng chung cho mọi lần phân tích chạy đồng thời, nên các hiện thực bắt buộc phải không giữ trạng thái.

---

## Phụ lục A — Hằng số chuỗi AI

`GS1Constants_AICodes` (gói `tools.pantheum.gaia.gs1.constants`) khai báo một hằng số `String` cho mọi Mã nhận dạng ứng dụng mà GAIA nhận biết. Hãy dùng những hằng số này thay vì viết cứng mã AI trong mã nguồn:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Mỗi hằng số mang dạng chuỗi của mã AI tương ứng (ví dụ `AI_01_GTIN = "01"`).

### Nhận dạng và đánh số sê-ri

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Mã công-ten-nơ vận chuyển nối tiếp (SSCC). |
| `01` | `AI_01_GTIN` | Mã số thương phẩm toàn cầu (GTIN). |
| `02` | `AI_02_CONTENT` | Mã số thương phẩm toàn cầu (GTIN) của các sản phẩm chứa bên trong. |
| `03` | `AI_03_MTO_GTIN` | Mã nhận dạng sản phẩm sản xuất theo đơn hàng (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Số lô hàng. |
| `20` | `AI_20_VARIANT` | Biến thể sản phẩm nội bộ. |
| `21` | `AI_21_SERIAL` | Số sê-ri. |
| `22` | `AI_22_CPV` | Biến thể sản phẩm tiêu dùng. |
| `235` | `AI_235_TPX` | Phần mở rộng có sê-ri của Mã số thương phẩm toàn cầu (GTIN) do bên thứ ba kiểm soát (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Mã nhận dạng sản phẩm bổ sung do nhà sản xuất cấp. |
| `241` | `AI_241_CUST_PART_NO` | Mã số linh kiện của khách hàng. |
| `242` | `AI_242_MTO_VARIANT` | Số biến thể sản xuất theo đơn hàng. |
| `243` | `AI_243_PCN` | Số thành phần đóng gói. |
| `250` | `AI_250_SECONDARY_SERIAL` | Số sê-ri phụ. |
| `251` | `AI_251_REF_TO_SOURCE` | Tham chiếu đến thực thể nguồn. |
| `253` | `AI_253_GDTI` | Mã nhận dạng loại tài liệu toàn cầu (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Thành phần mở rộng của mã số địa điểm toàn cầu (GLN). |
| `255` | `AI_255_GCN` | Mã số phiếu giảm giá toàn cầu (GCN). |
| `30` | `AI_30_VAR_COUNT` | Số lượng thay đổi của sản phẩm (sản phẩm có khối lượng thay đổi). |
| `37` | `AI_37_COUNT` | Số lượng sản phẩm hoặc phần sản phẩm chứa trong một đơn vị logistics. |

### Ngày và giờ

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Ngày sản xuất (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Ngày đến hạn (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Ngày đóng gói (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Hạn sử dụng tốt nhất (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Hạn bán (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Ngày hết hạn (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Thời gian giao hàng không sớm hơn (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Thời gian giao hàng không muộn hơn (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Ngày phát hành (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Ngày và giờ hết hạn (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Ngày cấp đông lần đầu (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Ngày thu hoạch (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Ngày cần kiểm tra trước (YYMMDD[hhmm]). |

### Số lượng và phép đo — Khối lượng thay đổi (hệ mét)

Các họ AI bốn chữ số `310n`–`369n` mã hóa những số lượng có khối lượng thay đổi. Chữ số thứ ba chọn loại phép đo; **chữ số thứ tư** (`n`, 0–5) là số chữ số thập phân ngầm định — vậy nên `AI_3102_NET_WEIGHT_KG` nghĩa là trọng lượng tịnh tính bằng kilôgam với 2 chữ số thập phân.

| Họ | Mẫu hằng số (`n` = chữ số thập phân) | Mô tả |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Trọng lượng tịnh, kilôgam (sản phẩm có khối lượng thay đổi). |
| `311n` | `AI_311n_LENGTH_M` | Chiều dài hoặc kích thước thứ nhất, mét (sản phẩm có khối lượng thay đổi). |
| `312n` | `AI_312n_WIDTH_M` | Chiều rộng, đường kính hoặc kích thước thứ hai, mét (sản phẩm có khối lượng thay đổi). |
| `313n` | `AI_313n_HEIGHT_M` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, mét (sản phẩm có khối lượng thay đổi). |
| `314n` | `AI_314n_AREA_M` | Diện tích, mét vuông (sản phẩm có khối lượng thay đổi). |
| `315n` | `AI_315n_NET_VOLUME_L` | Thể tích tịnh, lít (sản phẩm có khối lượng thay đổi). |
| `316n` | `AI_316n_NET_VOLUME_M` | Thể tích tịnh, mét khối (sản phẩm có khối lượng thay đổi). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Trọng lượng logistics, kilôgam. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Chiều dài hoặc kích thước thứ nhất, mét. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Chiều rộng, đường kính hoặc kích thước thứ hai, mét. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, mét. |
| `334n` | `AI_334n_AREA_M_LOG` | Diện tích, mét vuông. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Thể tích logistics, lít. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Thể tích logistics, mét khối. |
| `337n` | `AI_337n_KG_PER_M` | Kilôgam trên mét vuông. |

### Số lượng và phép đo — Khối lượng thay đổi (hệ Anh / Mỹ)

| Họ | Mẫu hằng số (`n` = chữ số thập phân) | Mô tả |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Trọng lượng tịnh, pound (sản phẩm có khối lượng thay đổi). |
| `321n` | `AI_321n_LENGTH_IN` | Chiều dài hoặc kích thước thứ nhất, inch (sản phẩm có khối lượng thay đổi). |
| `322n` | `AI_322n_LENGTH_FT` | Chiều dài hoặc kích thước thứ nhất, feet (sản phẩm có khối lượng thay đổi). |
| `323n` | `AI_323n_LENGTH_YD` | Chiều dài hoặc kích thước thứ nhất, yard (sản phẩm có khối lượng thay đổi). |
| `324n` | `AI_324n_WIDTH_IN` | Chiều rộng, đường kính hoặc kích thước thứ hai, inch (sản phẩm có khối lượng thay đổi). |
| `325n` | `AI_325n_WIDTH_FT` | Chiều rộng, đường kính hoặc kích thước thứ hai, feet (sản phẩm có khối lượng thay đổi). |
| `326n` | `AI_326n_WIDTH_YD` | Chiều rộng, đường kính hoặc kích thước thứ hai, yard (sản phẩm có khối lượng thay đổi). |
| `327n` | `AI_327n_HEIGHT_IN` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, inch (sản phẩm có khối lượng thay đổi). |
| `328n` | `AI_328n_HEIGHT_FT` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, feet (sản phẩm có khối lượng thay đổi). |
| `329n` | `AI_329n_HEIGHT_YD` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, yard (sản phẩm có khối lượng thay đổi). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Trọng lượng logistics, pound. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Chiều dài hoặc kích thước thứ nhất, inch. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Chiều dài hoặc kích thước thứ nhất, feet. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Chiều dài hoặc kích thước thứ nhất, yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Chiều rộng, đường kính hoặc kích thước thứ hai, inch. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Chiều rộng, đường kính hoặc kích thước thứ hai, feet. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Chiều rộng, đường kính hoặc kích thước thứ hai, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, inch. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, feet. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Chiều sâu, độ dày, chiều cao hoặc kích thước thứ ba, yard. |
| `350n` | `AI_350n_AREA_IN` | Diện tích, inch vuông (sản phẩm có khối lượng thay đổi). |
| `351n` | `AI_351n_AREA_FT` | Diện tích, feet vuông (sản phẩm có khối lượng thay đổi). |
| `352n` | `AI_352n_AREA_YD` | Diện tích, yard vuông (sản phẩm có khối lượng thay đổi). |
| `353n` | `AI_353n_AREA_IN_LOG` | Diện tích, inch vuông. |
| `354n` | `AI_354n_AREA_FT_LOG` | Diện tích, feet vuông. |
| `355n` | `AI_355n_AREA_YD_LOG` | Diện tích, yard vuông. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Trọng lượng tịnh, troy ounce (sản phẩm có khối lượng thay đổi). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Trọng lượng (hoặc thể tích) tịnh, ounce (sản phẩm có khối lượng thay đổi). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Thể tích tịnh, quart (sản phẩm có khối lượng thay đổi). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Thể tích tịnh, gallon Mỹ (sản phẩm có khối lượng thay đổi). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Thể tích logistics, quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Thể tích logistics, gallon Mỹ. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Thể tích tịnh, inch khối (sản phẩm có khối lượng thay đổi). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Thể tích tịnh, feet khối (sản phẩm có khối lượng thay đổi). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Thể tích tịnh, yard khối (sản phẩm có khối lượng thay đổi). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Thể tích logistics, inch khối. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Thể tích logistics, feet khối. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Thể tích logistics, yard khối. |

### Giá và số tiền

Chữ số thứ tư (`n`) mã hóa số chữ số thập phân ngầm định. Khoảng giá trị cho phép
khác nhau tùy họ — xem cột `n`.

| Họ | Mẫu hằng số (`n` = chữ số thập phân) | `n` | Mô tả |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Số tiền phải trả áp dụng hoặc giá trị phiếu giảm giá, tính theo nội tệ. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Số tiền phải trả áp dụng kèm mã tiền tệ ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Số tiền phải trả áp dụng, khu vực tiền tệ đơn (sản phẩm có khối lượng thay đổi). |
| `393n` | `AI_393n_PRICE` | 0–9 | Số tiền phải trả áp dụng kèm mã tiền tệ ISO (sản phẩm có khối lượng thay đổi). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Tỷ lệ phần trăm giảm giá của phiếu giảm giá. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Số tiền phải trả trên mỗi đơn vị đo, khu vực tiền tệ đơn (sản phẩm có khối lượng thay đổi). |

### Địa điểm và vận chuyển

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Số đơn đặt hàng của khách hàng. |
| `401` | `AI_401_GINC` | Mã số nhận dạng lô hàng toàn cầu (GINC). |
| `402` | `AI_402_GSIN` | Mã số nhận dạng lô hàng vận chuyển toàn cầu (GSIN). |
| `403` | `AI_403_ROUTE` | Mã định tuyến. |
| `410` | `AI_410_SHIP_TO_LOC` | Mã số địa điểm toàn cầu (GLN) nơi giao hàng/nhận hàng. |
| `411` | `AI_411_BILL_TO` | Mã số địa điểm toàn cầu (GLN) của bên nhận hóa đơn/thanh toán. |
| `412` | `AI_412_PURCHASE_FROM` | Mã số địa điểm toàn cầu (GLN) của nơi mua hàng. |
| `413` | `AI_413_SHIP_FOR_LOC` | Mã số địa điểm toàn cầu (GLN) của nơi chuyển tiếp giao/nhận hàng. |
| `414` | `AI_414_LOC_NO` | Mã nhận dạng địa điểm vật lý - Mã số địa điểm toàn cầu (GLN). |
| `415` | `AI_415_PAY_TO` | Mã số địa điểm toàn cầu (GLN) của bên xuất hóa đơn. |
| `416` | `AI_416_PROD_SERV_LOC` | Mã số địa điểm toàn cầu (GLN) của cơ sở sản xuất hoặc cung cấp dịch vụ. |
| `417` | `AI_417_PARTY` | Mã số địa điểm toàn cầu (GLN) của bên liên quan. |
| `420` | `AI_420_SHIP_TO_POST` | Mã bưu chính nơi giao hàng/nhận hàng trong phạm vi một cơ quan bưu chính. |
| `421` | `AI_421_SHIP_TO_POST` | Mã bưu chính nơi giao hàng/nhận hàng kèm mã quốc gia ISO. |
| `422` | `AI_422_ORIGIN` | Quốc gia xuất xứ của sản phẩm. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Quốc gia sơ chế ban đầu. |
| `424` | `AI_424_COUNTRY_PROCESS` | Quốc gia chế biến. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Quốc gia tháo dỡ. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Quốc gia bao quát toàn bộ chuỗi quy trình. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Vùng/tỉnh xuất xứ trong quốc gia. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Tên công ty nhận hàng. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Người liên hệ nhận hàng. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Địa chỉ giao hàng/nhận hàng - dòng 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Địa chỉ giao hàng/nhận hàng - dòng 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Khu vực (ngoại ô) nhận hàng. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Địa phương nhận hàng. |
| `4306` | `AI_4306_SHIP_TO_REG` | Vùng/miền nhận hàng. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Mã quốc gia nhận hàng. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Số điện thoại nhận hàng. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Vị trí địa lý nơi giao hàng/nhận hàng. |
| `4310` | `AI_4310_RTN_TO_COMP` | Tên công ty nhận trả hàng. |
| `4311` | `AI_4311_RTN_TO_NAME` | Người liên hệ nhận trả hàng. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Địa chỉ trả hàng - dòng 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Địa chỉ trả hàng - dòng 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Khu vực (ngoại ô) nhận trả hàng. |
| `4315` | `AI_4315_RTN_TO_LOC` | Địa phương nhận trả hàng. |
| `4316` | `AI_4316_RTN_TO_REG` | Vùng/miền nhận trả hàng. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Mã quốc gia nhận trả hàng. |
| `4318` | `AI_4318_RTN_TO_POST` | Mã bưu chính nhận trả hàng. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Số điện thoại nhận trả hàng. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Mô tả mã dịch vụ. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Cờ hiệu hàng nguy hiểm. |
| `4322` | `AI_4322_AUTH_LEAVE` | Cho phép để hàng không cần ký nhận. |
| `4323` | `AI_4323_SIG_REQUIRED` | Cờ hiệu yêu cầu ký nhận. |
| `4330` | `AI_4330_MAX_TEMP_F` | Nhiệt độ tối đa tính theo độ F (tính bằng phần trăm độ). |
| `4331` | `AI_4331_MAX_TEMP_C` | Nhiệt độ tối đa tính theo độ C (tính bằng phần trăm độ). |
| `4332` | `AI_4332_MIN_TEMP_F` | Nhiệt độ tối thiểu tính theo độ F (tính bằng phần trăm độ). |
| `4333` | `AI_4333_MIN_TEMP_C` | Nhiệt độ tối thiểu tính theo độ C (tính bằng phần trăm độ). |

### Thuộc tính sản phẩm và khả năng truy xuất nguồn gốc

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Mã số hàng dự trữ NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Phân loại thân thịt và các phần thịt cắt theo UN/ECE. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Hiệu lực hoạt tính. |
| `7005` | `AI_7005_CATCH_AREA` | Khu vực đánh bắt. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Loài dùng cho mục đích thủy sản. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Loại ngư cụ. |
| `7010` | `AI_7010_PROD_METHOD` | Phương pháp sản xuất. |
| `7020` | `AI_7020_REFURB_LOT` | Mã lô tân trang. |
| `7021` | `AI_7021_FUNC_STAT` | Tình trạng hoạt động. |
| `7022` | `AI_7022_REV_STAT` | Tình trạng phiên bản sửa đổi. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Mã nhận dạng tài sản riêng lẻ toàn cầu (GIAI) của một cụm lắp ráp. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Số hiệu đơn vị chế biến kèm mã quốc gia ISO ba chữ số (10 ô). |
| `7040` | `AI_7040_UIC_EXT` | Mã UIC của GS1 kèm phần mở rộng 1 và chỉ số nhà nhập khẩu. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Loại đơn vị hàng hóa theo UN/CEFACT. |

### Số hoàn trả y tế quốc gia (NHRN)

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Mã số hoàn trả y tế quốc gia (NHRN) - PZN của Đức. |
| `711` | `AI_711_NHRN_CIP` | Mã số hoàn trả y tế quốc gia (NHRN) - CIP của Pháp. |
| `712` | `AI_712_NHRN_CN` | Mã số hoàn trả y tế quốc gia (NHRN) - CN của Tây Ban Nha. |
| `713` | `AI_713_NHRN_DRN` | Mã số hoàn trả y tế quốc gia (NHRN) - DRN của Brazil. |
| `714` | `AI_714_NHRN_AIM` | Mã số hoàn trả y tế quốc gia (NHRN) - AIM của Bồ Đào Nha. |
| `715` | `AI_715_NHRN_NDC` | Mã số hoàn trả y tế quốc gia (NHRN) - NDC của Hoa Kỳ. |
| `716` | `AI_716_NHRN_AIC` | Mã số hoàn trả y tế quốc gia (NHRN) - AIC của Ý. |
| `717` | `AI_717_NHRN_SRN` | Mã số hoàn trả y tế quốc gia (NHRN) - Số đăng ký vệ sinh của Costa Rica. |

### Y tế, GMN, HIDRI, CPID và dữ liệu về người

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Số hiệu chứng nhận (10 ô). |
| `7240` | `AI_7240_PROTOCOL` | Mã giao thức. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Loại phương tiện AIDC. |
| `7242` | `AI_7242_VCN` | Số kiểm soát phiên bản (VCN). |
| `7250` | `AI_7250_DOB` | Ngày sinh (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Ngày và giờ sinh (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Giới tính sinh học. |
| `7253` | `AI_7253_FAMILY_NAME` | Họ của cá nhân. |
| `7254` | `AI_7254_GIVEN_NAME` | Tên riêng của cá nhân. |
| `7255` | `AI_7255_SUFFIX` | Hậu tố tên của cá nhân. |
| `7256` | `AI_7256_FULL_NAME` | Họ và tên đầy đủ của cá nhân. |
| `7257` | `AI_7257_PERSON_ADDR` | Địa chỉ của cá nhân. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Thứ tự sinh của em bé. |
| `7259` | `AI_7259_BABY` | Họ của em bé. |
| `8001` | `AI_8001_DIMENSIONS` | Sản phẩm dạng cuộn (chiều rộng, chiều dài, đường kính lõi, hướng cuộn, mối nối). |
| `8002` | `AI_8002_CMT_NO` | Mã nhận dạng số điện thoại di động. |
| `8003` | `AI_8003_GRAI` | Mã nhận dạng tài sản có thể hoàn trả toàn cầu (GRAI). |
| `8004` | `AI_8004_GIAI` | Mã nhận dạng tài sản riêng lẻ toàn cầu (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Đơn giá trên mỗi đơn vị đo. |
| `8006` | `AI_8006_ITIP` | Mã nhận dạng từng phần sản phẩm riêng lẻ (ITIP). |
| `8007` | `AI_8007_IBAN` | Số tài khoản ngân hàng quốc tế (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Ngày và giờ sản xuất (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Chỉ báo cảm biến đọc quang học. |
| `8010` | `AI_8010_CPID` | Mã nhận dạng linh kiện/bộ phận (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Số sê-ri của mã nhận dạng linh kiện/bộ phận (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Phiên bản phần mềm. |
| `8013` | `AI_8013_GMN` | Mã số model toàn cầu (GMN). |
| `8014` | `AI_8014_MUDI` | Mã nhận dạng đăng ký thiết bị cá thể hóa cao (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Mã số quan hệ dịch vụ toàn cầu (GSRN) nhận dạng mối quan hệ giữa tổ chức cung cấp dịch vụ và người cung cấp dịch vụ. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Mã số quan hệ dịch vụ toàn cầu (GSRN) nhận dạng mối quan hệ giữa tổ chức cung cấp dịch vụ và người nhận dịch vụ. |
| `8019` | `AI_8019_SRIN` | Số thực thể quan hệ dịch vụ (SRIN). |
| `8020` | `AI_8020_REF_NO` | Số tham chiếu phiếu thanh toán. |
| `8026` | `AI_8026_ITIP_CONTENT` | Mã nhận dạng các phần sản phẩm (ITIP) chứa trong một đơn vị logistics. |
| `8030` | `AI_8030_DIGSIG` | Chữ ký số (DigSig). |
| `8040` | `AI_8040_IMEI` | Mã nhận dạng thiết bị di động quốc tế (IMEI). |
| `8041` | `AI_8041_IMEI2` | Mã nhận dạng thiết bị di động quốc tế 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Số SIM nhúng. |
| `8043` | `AI_8043_PSIM` | Số SIM vật lý. |
| `8110` | `AI_8110` | Mã nhận dạng phiếu giảm giá dùng tại Bắc Mỹ. |
| `8111` | `AI_8111_POINTS` | Điểm thưởng của phiếu giảm giá. |
| `8112` | `AI_8112` | Mã nhận dạng phiếu giảm giá thuộc tệp ưu đãi hợp lệ (positive offer file) dùng tại Bắc Mỹ. |
| `8200` | `AI_8200_PRODUCT_URL` | URL đóng gói mở rộng. |

### Dùng nội bộ / trong công ty

| AI | Hằng số | Mô tả |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Thông tin được các bên thương mại thống nhất. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Thông tin nội bộ của công ty (9 ô). |

---

## Phụ lục B — Hằng số khóa diễn giải

Khi `GaiaParser.parse()` được gọi với `ParseMode.INTERPRETATION`, mỗi `GS1AIObjectElement` có thể mang một danh sách các đối tượng `GS1AIInterpretation` do những bộ làm giàu chuyên biệt theo lĩnh vực tạo ra. Hãy dùng các hằng số `GS1Constants_Enricher` (gói `tools.pantheum.gaia.gs1.constants`) làm khóa để tra cứu những giá trị diễn giải cụ thể:

```java
GS1AIObjectElement el = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Look up a single interpretation by type constant
GS1AIInterpretation fmt = el.getInterpretation(GS1Constants_Enricher.GTIN_TYPE);
if (fmt != null) System.out.println("GTIN type: " + fmt.getValue());

// Or iterate all interpretations
for (GS1AIInterpretation interp : el.getInterpretations()) {
    System.out.println(interp.getType() + " = " + interp.getValue());
}
```

Nhãn hiển thị **không** phải hằng số — chúng nằm trong các danh mục đã bản địa hóa ở `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, khóa theo hằng số kiểu. `GS1AIInterpretation.getLabel()` trả về nhãn bằng ngôn ngữ phân tích (xem [Thông báo và nhãn đã bản địa hóa](#thông-báo-và-nhãn-đã-bản-địa-hóa)), và quay về tiếng Anh khi một danh mục thiếu khóa đó. Cột Nhãn hiển thị dưới đây liệt kê văn bản tiếng Việt; bản thân các khóa kiểu thì không đổi giữa các ngôn ngữ, nên hãy luôn so khớp theo khóa, đừng bao giờ theo nhãn.

### Ngày và giờ

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `DATE_VALUE` | Ngày | Các AI ngày (11–17, 7003, 7006, 7011, v.v.) |
| `DATE_FORMAT` | Định dạng ngày | Các AI ngày |
| `TIME_VALUE` | Giờ | Các AI có mang giờ (7003, 7011, 8008, v.v.) |
| `TIME_FORMAT` | Định dạng giờ | Các AI có mang giờ |
| `DATETIME_VALUE` | Ngày và giờ | Các AI ngày+giờ |
| `DATETIME_FORMAT` | Định dạng ngày và giờ | Các AI ngày+giờ |

### Ngày thu hoạch

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Ngày bắt đầu thu hoạch | AI 7007 |
| `HARVEST_END_DATE` | Ngày kết thúc thu hoạch | AI 7007 (điểm cuối khoảng, tùy chọn) |
| `HARVEST_DATE_RANGE` | Khoảng ngày thu hoạch | AI 7007 |

### Tiền tố công ty GS1

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Tiền tố công ty GS1 | Các AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Mã thành viên GS1 | Các AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Tổ chức thành viên GS1 | Các AI GTIN / GLN / SSCC |

### GTIN

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Loại GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Cấp đóng gói | AI 01 |
| `GTIN_CHECK_DIGIT` | Chữ số kiểm tra | AI 01, 02 |

### SSCC

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Chữ số mở rộng | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Tham chiếu sê-ri | AI 00 |
| `SSCC_CHECK_DIGIT` | Chữ số kiểm tra | AI 00 |

### Quốc gia (ISO 3166)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Mã quốc gia (dạng số) | Các AI một quốc gia (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Mã quốc gia (alpha-2) | Các AI quốc gia dạng alpha-2 |
| `COUNTRY_NAME` | Tên quốc gia | Các AI một quốc gia |
| `COUNTRY_LIST` | Các quốc gia | AI 423 — mọi tên nối lại, ví dụ `Australia, New Zealand` |

AI 423 (quốc gia chế biến ban đầu) có thể mang tới năm quốc gia, nên nó xuất ra
**một cặp có đánh số cho mỗi quốc gia** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — rồi tiếp sau là một bản tóm tắt
`COUNTRY_LIST` duy nhất. Hãy dựng những khóa này từ các hằng số
`COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` với số thứ tự bắt đầu từ 1, hoặc
đơn giản là duyệt qua `getInterpretations()`; các khóa `COUNTRY_CODE_NUMERIC` /
`COUNTRY_NAME` không hậu tố **không** được xuất ra cho AI 423.

### Tiền tệ (ISO 4217)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Mã tiền tệ | Các AI số tiền kèm tiền tệ (391n, 393n) |
| `CURRENCY_ALPHA` | Mã chữ tiền tệ | Các AI số tiền kèm tiền tệ |
| `CURRENCY_NAME` | Tên tiền tệ | Các AI số tiền kèm tiền tệ |

### Nhiệt độ

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `TEMPERATURE` | Nhiệt độ | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Đơn vị nhiệt độ | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Nhiệt độ (đã định dạng) | AI 4330–4333 |

### Giới tính (ISO 5218)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `SEX_CODE` | Mã giới tính | AI 7252 |
| `SEX_DESCRIPTION` | Mô tả giới tính | AI 7252 |

### Loài thủy sinh (FAO)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Mã loài | AI 7008 |
| `SPECIES_SCIENTIFIC` | Tên khoa học | AI 7008 |
| `SPECIES_ENGLISH` | Tên thông thường | AI 7008 |
| `SPECIES_FAMILY` | Họ | AI 7008 |
| `SPECIES_ORDER` | Bộ | AI 7008 |

### Số hiệu kho NATO (NSN)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `NSN_FSG` | Nhóm cung cấp | AI 7001 |
| `NSN_FSG_NAME` | Tên nhóm cung cấp | AI 7001 |
| `NSN_FSCG` | Loại cung ứng | AI 7001 |
| `NSN_FSCG_NAME` | Tên lớp cung cấp | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Mã quốc gia | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Quốc gia | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Mã quốc gia ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Loại NCS | AI 7001 |
| `NSN_NIIN` | Số mục quốc gia | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Sản phẩm dạng cuộn

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Chiều rộng cuộn (mm) | AI 8001 |
| `ROLL_LENGTH` | Chiều dài cuộn (m) | AI 8001 |
| `CORE_DIAMETER` | Đường kính lõi (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Mã hướng cuộn | AI 8001 |
| `WINDING_DIRECTION` | Hướng cuộn | AI 8001 |
| `SPLICES` | Mối nối | AI 8001 |

### IBAN

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Mã quốc gia | AI 8007 |
| `IBAN_COUNTRY_NAME` | Quốc gia | AI 8007 |
| `IBAN_CHECK_DIGITS` | Chữ số kiểm tra | AI 8007 |
| `IBAN_CHECK_VALID` | Kiểm tra | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Số sê-ri | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Chữ số kiểm tra | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Cơ quan cấp | AI 8040, 8041 |

Mười lăm chữ số phân rã thành `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`, trong đó
RBI là hai chữ số đầu của TAC — nghĩa là `IMEI_RBI` là tiền tố của `IMEI_TAC` chứ không phải
một trường riêng. `IMEI_FORMATTED` hiển thị cách nhóm hiển thị chuẩn của GSMA
`AA-BBBBBB-CCCCCC-D` (ví dụ `49-015420-323751-8`), vốn cắt TAC tại ranh giới RBI; cách nhóm
cũ `6-2-6-1`, vốn cắt tại chỗ mã lắp ráp cuối cùng nay đã bãi bỏ từng bắt đầu, không được
xuất ra.

`IMEI_RBI_NAME` phân giải RBI thành tên cơ quan cấp phát thông qua `ImeiRbiData`, và nó chỉ
được gắn kèm **sau cùng và chỉ khi mã ấy có trong bảng đó**. Bảng ấy bao trùm ba nhóm:

- **Các cơ quan hiện vẫn cấp phát** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, cùng với
  `99` Global Hexadecimal Administrator và `98` (dành riêng).
- **Các khoảng thử nghiệm** — `00` và `02`–`09`, biểu thị IMEI thử nghiệm chứ không phải một
  lần cấp phát thật. Hãy hỏi chúng bằng `ImeiRbiData.isTestCode(code)`.
- **Các cơ quan không còn cấp phát** — những cơ quan lịch sử như `49` (BZT/BAPT, Đức), `44`
  (BABT, Anh) và `91` (MSAI, Ấn Độ). Hãy hỏi chúng bằng
  `ImeiRbiData.isNoLongerAllocating(code)`. Thiết bị mang những mã này là bình thường và vẫn
  đang hoạt động; chỉ việc cấp phát mới là đã dừng, nên đây là thông tin để báo cáo, tuyệt
  nhiên không phải dấu hiệu về tính hợp lệ.

`IMEI_RBI_NAME` vắng mặt nghĩa là "RBI này không có trong bảng của chúng tôi", **chứ không**
phải "IMEI không hợp lệ": bảng ấy được tổng hợp từ một danh sách RBI đã công bố chứ không lấy
thẳng từ GSMA, nên nó có thể chậm hơn so với những cơ quan vừa được chỉ định. Đừng rút ra bất
kỳ phán quyết hợp lệ nào từ sự vắng mặt của nó; RBI không phải một ký tự kiểm tra. Mã nguồn
duyệt qua danh sách diễn giải nên chịu được việc nó vắng mặt, thay vì lập chỉ mục theo vị trí.

### Mã nhận dạng SIM (EID / ICCID)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Nhóm ngành | AI 8042 |
| `EID_BODY` | Phần thân EID | AI 8042 |
| `EID_CHECK_DIGIT` | Chữ số kiểm tra | AI 8042 |
| `ICCID_BODY` | Phần thân ICCID | AI 8043 |
| `ICCID_EXTENSION` | Mở rộng | AI 8043 |

`SIM_MII` mang **hai** chữ số đầu (`89`), tức cặp số mà ITU-T E.118 dành cho viễn thông. Bản
thân ISO/IEC 7812 lại định nghĩa MII là **chỉ chữ số đầu tiên**, nên `SIM_MII_NAME` phân giải
nhóm ngành từ chữ số `8` đứng đầu thông qua `Iso7812Data` — cho ra "Healthcare,
telecommunications and other future industry assignments". Bởi vậy nó không đổi với mọi EID
đúng dạng; nó được báo cáo để truy vết về tiêu chuẩn, chứ không phải như một dấu hiệu phân
biệt. `Iso7812Data.nameForCode(digit)` nhận một chữ số đơn lẻ, còn
`nameForIdentifier(prefix)` nhận một tiền tố dài hơn và đọc chữ số đầu của nó.

`SIM_MII_NAME` chỉ do bộ làm giàu `EidEnricher` (AI 8042) xuất ra. `IccidEnricher` (AI 8043)
có hiển thị `SIM_MII` nhưng không kèm nhóm ngành.

### Số hiệu chứng nhận

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Số thứ tự | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Mã chương trình chứng nhận | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Chương trình chứng nhận | AI 7230–7239 |
| `CERT_REFERENCE` | Tham chiếu chứng nhận | AI 7230–7239 |

### GS1 UIC

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `UIC_CODE` | Mã UIC | AI 7040 |
| `UIC_EXTENSION_1` | Mở rộng 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Chỉ số nhà nhập khẩu | AI 7040 |

### Thứ tự sinh của trẻ sơ sinh

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Vị trí sinh | AI 7258 |
| `BIRTH_TOTAL` | Tổng số sinh | AI 7258 |
| `BIRTH_SEQUENCE` | Trình tự sinh | AI 7258 |

### Số hiệu mẫu toàn cầu (GMN)

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Tham chiếu mẫu | AI 8013 |
| `GMN_CHECK_PAIR` | Cặp kiểm tra | AI 8013 |

### HIDRI

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Tham chiếu thiết bị | AI 8014 |
| `HIDRI_CHECK_PAIR` | Cặp kiểm tra | AI 8014 |

### CPID

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Tham chiếu linh kiện & bộ phận | AI 8010–8011 |

### Giá trị thập phân và giá trị đo lường

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Giá trị thập phân | Các AI dạng số có chữ số thập phân ngầm định (31xx–36xx) |
| `DECIMAL_AMOUNT` | Số tiền | Các AI giá (390n–395n) |
| `DECIMAL_PERCENTAGE` | Phần trăm | AI 394n |
| `DECIMAL_PLACES` | Chữ số thập phân | Cùng với `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Định dạng phần trăm | AI 394n |
| `ISO_UNIT_CODE` | Mã đơn vị ISO | Các AI đo lường |
| `ISO_UNIT_NAME` | Tên đơn vị ISO | Các AI đo lường |
| `MONETARY_AMOUNT` | Số tiền | Các AI giá |
| `MONETARY_AMOUNT_DISPLAY` | Số tiền (đã định dạng) | Các AI giá |

### Tọa độ địa lý

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `LATITUDE` | Vĩ độ | AI 4309 |
| `LONGITUDE` | Kinh độ | AI 4309 |
| `GEO_COORDINATES` | Tọa độ địa lý | AI 4309 |
| `LATITUDE_DMS` | Vĩ độ (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Kinh độ (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Tọa độ địa lý (DMS) | AI 4309 |

### Phương pháp sản xuất

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Mã phương pháp sản xuất | AI 7010 |
| `PRODUCTION_METHOD` | Phương pháp sản xuất | AI 7010 |

### Loại vật trung gian AIDC

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Mã loại phương tiện AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Loại phương tiện AIDC | AI 7241 |

### Phần trên tổng

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Số mảnh | AI 8006 |
| `PIECE_TOTAL` | Tổng số mảnh | AI 8006 |
| `PIECE_OF_TOTAL` | Mảnh trên tổng | AI 8006 |

### Tách thành phần

Những khóa do các phép tách thành phần khai báo trong `content/ai-content.json` xuất ra, chứ
không phải do một bộ làm giàu viết bằng Java — tất cả đều cho thấy những phần có tên của một
giá trị AI phức hợp. Khác với mọi khóa khác trong phụ lục này, **chúng không có hằng số trong
`GS1Constants_Enricher`**: hãy so khớp theo chuỗi ký tự, hoặc đọc kiểu từ
`GS1AIInterpretation.getType()`.

| Khóa kiểu | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Chữ số kiểm tra | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Số sê-ri | AI 253, 255, 8003 |
| `POSTAL_CODE` | Mã bưu chính | AI 421 |
| `PROCESSOR_ID` | Mã định danh bên xử lý | AI 7030–7039 |

Lưu ý rằng `CHECK_DIGIT` ở đây là khóa tách thành phần dùng chung, khác với các khóa riêng của
bộ làm giàu là `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` và
`EID_CHECK_DIGIT` nêu ở trên.

### Khác

| Hằng số khóa | Nhãn hiển thị | Do đâu tạo ra |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Giá trị | Các AI luận lý / cờ (4321–4323) |
| `DECODED_TEXT` | Văn bản đã giải mã | Các AI văn bản tự do |
