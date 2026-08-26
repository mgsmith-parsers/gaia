# GaiaBuilder — Hướng dẫn cho lập trình viên

## Mục lục

1. [Tổng quan](#tổng-quan)
2. [Về GS1 và General Specifications](#về-gs1-và-general-specifications)
3. [Hướng dẫn nhanh](#hướng-dẫn-nhanh)
4. [Cách hoạt động](#cách-hoạt-động)
5. [Dựng chuỗi phần tử](#dựng-chuỗi-phần-tử)
   - [AI thuộc tính cần khóa nhận dạng của mình](#ai-thuộc-tính-cần-khóa-nhận-dạng-của-mình)
6. [Dựng URI Digital Link](#dựng-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Kiểm tra hợp lệ và lỗi](#kiểm-tra-hợp-lệ-và-lỗi)
   - [Những phương thức dựng có ném ngoại lệ](#những-phương-thức-dựng-có-ném-ngoại-lệ)
   - [Những phương thức tryBuild\* không ném ngoại lệ](#những-phương-thức-trybuild-không-ném-ngoại-lệ)
   - [Ngôn ngữ của thông báo lỗi](#ngôn-ngữ-của-thông-báo-lỗi)
   - [BuildResult](#buildresult)
9. [Chữ số kiểm tra](#chữ-số-kiểm-tra)
10. [An toàn luồng](#an-toàn-luồng)
11. [Tham chiếu API](#tham-chiếu-api)

---

## Tổng quan

`GaiaBuilder` là bản đối ứng theo chiều ngược của [`GaiaParser`](GaiaParser-Vietnamese.md): nó biến một tập các cặp Mã nhận dạng ứng dụng (AI) và giá trị thành một **chuỗi phần tử** GS1 đúng dạng hoặc một **URI GS1 Digital Link**. Bạn cung cấp các AI cùng giá trị dữ liệu đầy đủ của chúng; bộ dựng ghép chúng lại, kiểm tra hợp lệ kết quả bằng chính bộ máy mà `GaiaParser` dùng, rồi kết xuất.

Vì bộ dựng kiểm tra hợp lệ bằng cách *tự phân tích chính kết quả dự kiến của mình*, nên mọi thứ nó trả về đều bảo đảm sẽ được `GaiaParser` phân tích trót lọt — hai bên không thể nào bất đồng về việc thế nào là đúng dạng.

**Lớp điểm vào:** `tools.pantheum.gaia.GaiaBuilder`

---

## Về GS1 và General Specifications

**GS1** là một tổ chức phi lợi nhuận toàn cầu xây dựng và duy trì các tiêu chuẩn mở cho việc nhận dạng và trao đổi dữ liệu trong chuỗi cung ứng. Các tiêu chuẩn của tổ chức này được dùng trong bán lẻ, y tế, hậu cần, dịch vụ ăn uống và nhiều ngành khác, bao trùm mọi thứ từ mã vạch sản phẩm trên bao bì tiêu dùng cho đến việc theo dõi từng liều thuốc theo số sê-ri.

Nguồn tham chiếu chính thức cho mọi điều mà bộ dựng này hiện thực là **GS1 General Specifications** — một tài liệu duy nhất định nghĩa:

- Toàn bộ mã Mã nhận dạng ứng dụng (AI), tiêu đề dữ liệu, định dạng và quy tắc kiểm tra hợp lệ của chúng
- Các quy tắc cú pháp để tạo và mã hóa chuỗi phần tử AI
- Yêu cầu về hệ ký hiệu mã vạch và việc cấp phát Mã nhận dạng AIM
- Thuật toán chữ số kiểm tra và ký tự kiểm tra
- Cách xác định năm hai chữ số (quy tắc cửa sổ trượt)
- Đặc tả của Data Matrix, QR Code, GS1-128, GS1 DataBar và các vật mang khác

GS1 General Specifications được cập nhật hằng năm. Ấn bản hiện hành và tài liệu bổ trợ có tại:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA hiện thực **Bản phát hành 26.0 (đã phê chuẩn, tháng 1/2026)** của GS1 General Specifications.

URI GS1 Digital Link chịu sự điều chỉnh của một tiêu chuẩn đi kèm, **GS1 Digital Link: URI Syntax**, vốn định nghĩa các khóa nhận dạng chính, thứ tự của các bổ ngữ khóa, và cách mã hóa thuộc tính dữ liệu mà bộ dựng áp dụng khi kết xuất URI Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA hiện thực **Bản phát hành 1.7.0 (đã phê chuẩn, tháng 8/2026)** của tiêu chuẩn GS1 Digital Link: URI Syntax.

Các tham chiếu mục trong tài liệu này trỏ tới GS1 General Specifications (ví dụ "Table 7-5", "section 7.12"), ngoại trừ số mục của Digital Link (ví dụ "§4.9", "§4.12") vốn trỏ tới tiêu chuẩn GS1 Digital Link: URI Syntax.

---

## Hướng dẫn nhanh

```java
import tools.pantheum.gaia.GaiaBuilder;

// Element string
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("10", "LOT-ABC")
        .ai("17", "271231")
        .buildElementString();
// 0109506000134352 10 LOT-ABC <GS> 17 271231   (GS = FNC1 group separator, 0x1D)

// GS1 Digital Link URI (canonical, on https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("10", "LOT-ABC")
        .ai("17", "271231")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
```

Hãy dùng hằng số `GS1Constants_AICodes` thay vì viết thẳng mã AI (xem [Phụ lục A trong hướng dẫn bộ phân tích](GaiaParser-Vietnamese.md#phụ-lục-a--hằng-số-chuỗi-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Cách hoạt động

Mỗi lần dựng đều đi theo cùng một lộ trình:

1. **Ghép nối** — các cặp AI/giá trị được nối lại thành một chuỗi phần tử dự kiến. Dấu phân tách nhóm FNC1 (`0x1D`) được chèn sau mỗi AI *cần dấu phân tách* và không phải phần tử cuối. Những AI đã biết trước độ dài (GTIN, ngày tháng, phép đo độ dài cố định) không nhận dấu phân tách; các AI còn lại thì có. (AI không rõ chẳng bao giờ đi tới bước này — `ai(...)` từ chối chúng ngay lập tức; xem [Dựng chuỗi phần tử](#dựng-chuỗi-phần-tử).)
2. **Kiểm tra hợp lệ** — chuỗi dự kiến được `GaiaParser` phân tích ở chế độ `CONTENT`. Mỗi giá trị được đối chiếu với định dạng và chữ số kiểm tra của AI tương ứng, và các quy tắc cấu trúc (những cặp AI bắt buộc và bị loại trừ) được thực thi. Nếu kết quả phân tích không hợp lệ, việc dựng thất bại.
3. **Kết xuất** —
   - Với chuỗi phần tử, `toElementString()` của đối tượng đã kiểm tra hợp lệ được trả về.
   - Với Digital Link, mỗi phần tử được gán vai trò DL của nó (khóa chính, bổ ngữ khóa, hoặc thuộc tính dữ liệu), chuỗi bổ ngữ khóa được kiểm tra hợp lệ, URI được sinh ra, rồi **URI vừa sinh được phân tích lại để chắc chắn nó quay về đúng như một Digital Link hợp lệ** — đây là một phép kiểm tra an toàn cho bước ghép chuỗi và mã hóa phần trăm. Nếu không quay về được, `GaiaBuilderException` sẽ được ném ra.

Điều này phản chiếu chính logic tái dựng trong `DLSyntaxParser`, nên vị trí đặt dấu phân tách và việc kiểm tra hợp lệ khớp đúng những gì bộ phân tích trông đợi.

---

## Dựng chuỗi phần tử

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** được kiểm tra hợp lệ ngay lập tức: `ai(...)` ném `IllegalArgumentException` nếu nó không phải một Mã nhận dạng ứng dụng GS1 đã biết. (Bộ dựng nối AI với giá trị trước khi phân tích, nên một AI không rõ hay quá dài như `"99999"` buộc phải bị bắt ngay ở đây — nếu không, nó sẽ lặng lẽ bị tách token lại thành một AI khác.) **Giá trị** được kiểm tra hợp lệ muộn hơn, vào lúc dựng.
- Giá trị phải **đầy đủ**, bao gồm cả chữ số kiểm tra. Bộ dựng không tính và cũng không thêm chữ số kiểm tra giúp bạn — xem [Chữ số kiểm tra](#chữ-số-kiểm-tra).
- Các AI được kết xuất theo đúng thứ tự bạn thêm vào. Bộ dựng tự chèn dấu phân tách FNC1 ở những chỗ cấu trúc GS1 đòi hỏi; đừng tự thêm chúng.
- Dựng mà **không có AI nào** sẽ ném `GaiaBuilderException("No AIs supplied")` với `getErrors()` rỗng — đây là thất bại duy nhất không mang theo `GaiaError` nào.
- Một AI có giá trị không đạt quy tắc định dạng hay chữ số kiểm tra của nó sẽ khiến cả lần dựng thất bại.

### AI thuộc tính cần khóa nhận dạng của mình

Phần lớn AI là *thuộc tính* mà GS1 General Specifications đòi hỏi phải đi kèm một khóa nhận dạng, và bộ dựng thực thi điều đó — nó kiểm tra hợp lệ qua trọn vẹn giai đoạn cú pháp, không có đường vòng nào. Một số lô hay số sê-ri đứng một mình **không** phải chuỗi phần tử hợp lệ:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Các khóa nhận dạng (GTIN `01`, SSCC `00`, GLN `414`, …) và những AI nội bộ công ty (`90`–`99`) hoàn toàn chính đáng khi đứng một mình. Mọi thứ còn lại đều cần một AI đi kèm.

> Có thể bảo `GaiaParser` bỏ qua phép kiểm tra này bằng `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` cố ý không cung cấp thứ tương đương — mục đích của nó là tạo ra kết quả tuân thủ tiêu chuẩn. Muốn ghép một chuỗi phần tử cố ý để khuyết, hãy tự ghép rồi phân tích với phép kiểm tra ấy đã tắt.

---

## Dựng URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Một Digital Link hợp lệ cần đúng một **khóa nhận dạng chính** (ví dụ GTIN `01`, GLN `414`, SSCC `00`). Bộ dựng phân loại mọi AI bạn cung cấp:

| Vai trò | Được kết xuất ra sao | Ví dụ |
|------|-------------|---------|
| Khóa nhận dạng chính | Đoạn đường dẫn ngay sau tên miền/tiền tố | `/01/09506000134352` |
| Bổ ngữ khóa (CPV `22`, lô `10`, sê-ri `21`, …) | Các đoạn đường dẫn tiếp theo, **theo thứ tự chuẩn tắc của §4.9** (không phải thứ tự bạn thêm vào) | `/10/LOT-ABC` |
| Thuộc tính dữ liệu (mọi thứ còn lại) | Tham số truy vấn, **sắp theo thứ tự từ điển của khóa AI** (§4.12) | `?17=271231` |

Vì các bổ ngữ được sắp xếp lại khi kết xuất, việc cung cấp chúng lệch thứ tự cũng chẳng sao — `ai("21", …)` đặt trước `ai("10", …)` vẫn được kết xuất thành `/10/LOT/21/SER`. Chỉ *tập hợp* của chúng mới cần được khóa chính chấp nhận.

Giá trị ở cả đường dẫn lẫn truy vấn đều được mã hóa phần trăm.

Việc dựng thất bại (ném `GaiaBuilderException`, hoặc trả về một `BuildResult` thất bại) khi:

- **không có** khóa nhận dạng chính nào trong các AI;
- có **nhiều hơn một** khóa nhận dạng chính;
- một AI nào đó **bị cấm** trong Digital Link (`03`, `8014`);
- **chuỗi bổ ngữ khóa** không hợp lệ với khóa chính đã chọn (một bổ ngữ không đi cùng khóa ấy, hoặc các bổ ngữ nằm ngoài thứ tự được phép).

---

## BuilderDigitalLinkConfig

Hãy truyền một `BuilderDigitalLinkConfig` để kiểm soát lược đồ, tên miền, tiền tố đường dẫn, các tham số truy vấn bổ sung và phân đoạn:

```java
import tools.pantheum.gaia.config.BuilderDigitalLinkConfig;

BuilderDigitalLinkConfig cfg = BuilderDigitalLinkConfig.builder()
        .baseUrl("https://example.com/resolver")   // sets scheme, domain, and path prefix at once
        .addQueryParam("context", "retail")        // appended after the AI data attributes
        .fragment("section-2")
        .build();

String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildDigitalLinkUri(cfg);
// https://example.com/resolver/01/09506000134352?context=retail#section-2
```

| Phương thức của bộ dựng | Mục đích | Mặc định |
|----------------|---------|---------|
| `scheme(String)` | Lược đồ URI; phải là `http` hoặc `https` | `https` |
| `domain(String)` | Thẩm quyền phân giải — máy chủ hoặc `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Các đoạn đường dẫn đứng trước khóa chính đầu tiên; dấu gạch chéo ở hai đầu được chuẩn hóa | *(không có)* |
| `baseUrl(String)` | Tiện ích tách một URL thành `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Tham số truy vấn bổ sung, được nối **sau** các thuộc tính dữ liệu AI, theo thứ tự thêm vào; có mã hóa phần trăm | — |
| `fragment(String)` | Phân đoạn URI (không có dấu `#` ở đầu); có mã hóa phần trăm | *(không có)* |

`build()` kiểm tra hợp lệ cấu hình ngay lập tức: một lược đồ không phải `http(s)` hay một tên miền rỗng sẽ ném `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (bí danh `defaultConfig()`) chính là mặc định `https://id.gs1.org` không kèm gì thêm — đúng thứ mà `buildDigitalLinkUri()` không tham số dùng, và cũng là thứ mà `GS1AIObject.getCanonicalDigitalLink()` tạo ra.
- `baseUrl("http://id.example.org:8080/r")` → lược đồ `http`, tên miền `id.example.org:8080`, tiền tố đường dẫn `/r`.
- Các tham số truy vấn bổ sung luôn đứng sau những thuộc tính bắt nguồn từ AI, nhờ đó thứ tự AI chuẩn tắc (§4.12) được giữ nguyên.

`BuilderDigitalLinkConfig` là bất biến; cứ thoải mái dùng lại một thể hiện duy nhất.

---

## Kiểm tra hợp lệ và lỗi

### Những phương thức dựng có ném ngoại lệ

`buildElementString()`, `buildDigitalLinkUri()` và `buildDigitalLinkUri(BuilderDigitalLinkConfig)` ném **`GaiaBuilderException`** (một `RuntimeException` không bị kiểm tra) khi các AI không thể tạo nên một kết quả đúng dạng:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Với những thất bại về **nội dung** (sai chữ số kiểm tra, lệch định dạng, AI thiếu/bị cấm), `getErrors()` mang các đối tượng `GaiaError` của bộ phân tích — đúng những đối tượng [được mô tả trong hướng dẫn bộ phân tích](GaiaParser-Vietnamese.md#gaiaerror).
- Với những thất bại về **cấu trúc Digital Link** (thiếu khóa chính, nhiều khóa chính, AI bị cấm, chuỗi bổ ngữ khóa không hợp lệ), `getErrors()` mang một `GaiaError` duy nhất (mã `GE-L008`, `GE-L012`, `GE-L013` hoặc `GE-L014`) đã bản địa hóa sang ngôn ngữ của bộ dựng.

### Những phương thức tryBuild\* không ném ngoại lệ

Khi đầu vào đến từ người dùng và thất bại là một kết cục lường trước, xử lý được, hãy dùng các biến thể `tryBuild*` thay vì điều khiển luồng bằng ngoại lệ. Chúng trả về một [`BuildResult`](#buildresult) thay vì ném:

```java
BuildResult r = GaiaBuilder.create()
        .ai("01", userValue)
        .tryBuildElementString();

if (r.isSuccess()) {
    use(r.getValue());
} else {
    report(r.getMessage(), r.getErrors());
}
```

| Có ném | Không ném |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Mỗi phương thức `tryBuild*` dùng chung phần lõi kiểm tra hợp lệ với người anh em có ném của nó; chỉ khác ở ranh giới xử lý thất bại.

### Ngôn ngữ của thông báo lỗi

Lỗi kiểm tra nội dung được lấy từ danh mục lỗi đã bản địa hóa. Hãy gọi `language(...)` để chọn ngôn ngữ cho các thông báo `GaiaError` mà `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` mang theo; mặc định là tiếng Anh:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Đây chính là thiết lập `GaiaConstants.Language` mà `GaiaParser` nhận qua `ParseConfig`, nên bộ dựng và bộ phân tích bản địa hóa theo cùng một cách.

Thông báo `GaiaError` cho cả thất bại về **nội dung** lẫn về **cấu trúc Digital Link** (thiếu khóa chính, nhiều khóa chính, AI bị cấm, chuỗi bổ ngữ khóa không hợp lệ) đều được bản địa hóa qua danh mục lỗi dùng chung — nhóm sau dùng các mã `GE-L008`, `GE-L012`, `GE-L013` và `GE-L014`.

### BuildResult

`BuildResult` (gói `tools.pantheum.gaia.result`) là một kiểu giá trị bất biến mô tả kết quả của một lần gọi `tryBuild*`:

| Phương thức | Khi thành công | Khi thất bại |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | Chuỗi đã kết xuất | `null` |
| `getMessage()` | `null` | Mô tả về thất bại |
| `getErrors()` | Danh sách rỗng | Lỗi kiểm tra hợp lệ (giống như trong `GaiaBuilderException.getErrors()`) |

---

## Chữ số kiểm tra

Bộ dựng kiểm tra hợp lệ chữ số kiểm tra nhưng **không** tính chúng — giá trị của bạn phải sẵn có chữ số kiểm tra. Để tính một chữ số, hãy dùng `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` áp dụng thuật toán modulo-10 chuẩn của GS1 lên các chữ số đã cho và trả về một chữ số kiểm tra từ `0` đến `9`, hoặc `-1` nếu đầu vào là `null`, rỗng hoặc không phải chữ số.

---

## An toàn luồng

`GaiaBuilder` **không** an toàn luồng và được thiết kế để dùng một lần: gọi `create()`, thêm các AI, dựng một lần. Hãy tạo bộ dựng mới cho mỗi kết quả; đừng dùng chung một bộ dựng giữa các luồng.

`BuilderDigitalLinkConfig` (cùng những `BuildResult` mà nó sinh ra) là bất biến và có thể dùng chung thoải mái — hãy dựng một cấu hình lúc khởi động rồi dùng lại nó cho nhiều bộ dựng.

---

## Tham chiếu API

### `GaiaBuilder`

| Phương thức | Mô tả |
|--------|-------------|
| `static GaiaBuilder create()` | Khởi tạo một bộ dựng mới, rỗng. |
| `GaiaBuilder ai(String ai, String value)` | Thêm một AI cùng giá trị đầy đủ của nó. Ném `IllegalArgumentException` nếu một trong hai là `null`, hoặc nếu `ai` không phải một Mã nhận dạng ứng dụng GS1 đã biết. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Đặt ngôn ngữ cho thông báo lỗi kiểm tra nội dung (mặc định là tiếng Anh). `null` bị bỏ qua. |
| `String buildElementString()` | Kết xuất một chuỗi phần tử GS1. Ném `GaiaBuilderException` khi thất bại. |
| `String buildDigitalLinkUri()` | Kết xuất một URI Digital Link chuẩn tắc. Ném `GaiaBuilderException` khi thất bại. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Kết xuất một URI Digital Link theo `config`. Ném `GaiaBuilderException` khi thất bại. |
| `BuildResult tryBuildElementString()` | Dựng chuỗi phần tử, không ném ngoại lệ. |
| `BuildResult tryBuildDigitalLinkUri()` | Dựng Digital Link chuẩn tắc, không ném ngoại lệ. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Dựng Digital Link theo `config`, không ném ngoại lệ. |

### `BuilderDigitalLinkConfig`

| Thành phần | Mô tả |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Mặc định `https://id.gs1.org`. |
| `static Builder builder()` | Một bộ dựng cấu hình mới. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Lược đồ, thẩm quyền phân giải và tiền tố đường dẫn sau khi phân giải. |
| `getExtraQueryParams()` | Các tham số truy vấn bổ sung, theo thứ tự thêm vào. |
| `getFragment()` | Phân đoạn, hoặc `null`. |

### `GaiaBuilderException`

| Thành phần | Mô tả |
|--------|-------------|
| `getErrors()` | Những đối tượng `GaiaError` đã gây ra thất bại — lỗi của bộ phân tích với thất bại về nội dung, hoặc một lỗi cấu trúc Digital Link duy nhất (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Không bao giờ là `null`. |

### `BuildResult`

| Thành phần | Mô tả |
|--------|-------------|
| `isSuccess()` | Việc dựng có thành công hay không. |
| `getValue()` | Kết quả đã kết xuất khi thành công; `null` khi thất bại. |
| `getMessage()` | Mô tả thất bại khi thất bại; `null` khi thành công. |
| `getErrors()` | Lỗi kiểm tra hợp lệ khi thất bại; rỗng khi thành công. Không bao giờ là `null`. |
| `getTiming()` | `ProcessingTiming` của thao tác dựng (thời điểm bắt đầu, thời lượng xử lý), hoặc `null`. |

---

Xem thêm: **[GaiaParser — Hướng dẫn cho lập trình viên](GaiaParser-Vietnamese.md)** để biết về phía phân tích, mô hình phần tử AI, tham chiếu lỗi, cùng các phụ lục về hằng số AI và hằng số diễn giải.
