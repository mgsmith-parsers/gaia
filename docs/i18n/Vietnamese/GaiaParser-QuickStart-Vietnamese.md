# GaiaParser — Hướng dẫn nhanh

Biến một phần tải mã vạch GS1 thành dữ liệu có cấu trúc, đã kiểm tra hợp lệ và con người đọc
được, trong khoảng mười phút. Đây là lối tắt; **[Hướng dẫn cho lập trình viên
GaiaParser](GaiaParser-Vietnamese.md)** mới là bản tham chiếu đầy đủ, còn
**[GaiaBuilder](GaiaBuilder-Vietnamese.md)** nói về chiều ngược lại (dựng chuỗi phần tử và URI Digital
Link).

## Mục lục

1. [Thêm Gaia vào dự án của bạn](#1-thêm-gaia-vào-dự-án-của-bạn)
2. [Phân tích thử một chuỗi](#2-phân-tích-thử-một-chuỗi)
3. [Đọc kết quả](#3-đọc-kết-quả)
4. [Xử lý một lần phân tích thất bại](#4-xử-lý-một-lần-phân-tích-thất-bại)
5. [Hai điều sẽ làm bạn vấp](#5-hai-điều-sẽ-làm-bạn-vấp)
6. [Tiền tố của máy quét và Digital Link đều chạy được ngay](#6-tiền-tố-của-máy-quét-và-digital-link-đều-chạy-được-ngay)
7. [Làm ít việc hơn: các chế độ phân tích](#7-làm-ít-việc-hơn-các-chế-độ-phân-tích)
8. [Đổi ngôn ngữ và định dạng ngày](#8-đổi-ngôn-ngữ-và-định-dạng-ngày)
9. [Dọn dẹp đầu vào lộn xộn](#9-dọn-dẹp-đầu-vào-lộn-xộn)
10. [Đi tiếp từ đâu](#10-đi-tiếp-từ-đâu)

---

## 1. Thêm Gaia vào dự án của bạn

Gaia không được phát hành trên Maven Central, nên hãy dựng phần lõi một lần rồi cài nó vào
kho cục bộ của bạn:

```bash
cd gaia && mvn install
```

Rồi khai báo phụ thuộc:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Đó là toàn bộ phần phụ thuộc bạn cần viết. Gói jar khá gọn: phụ thuộc duy nhất ở phạm vi biên
dịch của Gaia — `com.fasterxml.jackson.core:jackson-databind` — đi kèm theo dạng bắc cầu; và
nếu bản dựng của bạn vốn đã ghim một phiên bản Jackson, thì chính bản ghim ấy thắng thế và
Gaia dùng nó. Gaia nhắm tới **Java 11**, và cùng một gói jar chạy nguyên vẹn trên mọi bản
JVM về sau.

> Bỏ qua bộ kiểm thử của phần lõi (`mvn install -DskipTests`) sẽ rút vài phút xuống còn vài
> giây trong lúc bạn mới bắt đầu.

---

## 2. Phân tích thử một chuỗi

Một lớp duy nhất, không cần cấu hình:

```java
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.result.ParseResult;

public class Hello {

    // Reuse one parser: it is thread-safe and does its dataset loading once.
    private static final GaiaParser PARSER = new GaiaParser();

    public static void main(String[] args) {
        ParseResult result = PARSER.parse("01095060001343521726123110LOT-001");

        System.out.println("valid : " + result.isValid());
        System.out.println("HRI   : " + result.getAiObject().toHriString());

        for (GS1AIObjectElement e : result.getAiObject().getAis()) {
            System.out.printf("(%s) %-18s = %s%n", e.getAi(), e.getTitle(), e.getValue());
        }
    }
}
```

```
valid : true
HRI   : (01)09506000134352 (17)261231 (10)LOT-001
(01) GTIN               = 09506000134352
(17) USE BY or EXPIRY   = 261231
(10) BATCH/LOT          = LOT-001
```

`parse(String)` chạy **trọn** chuỗi xử lý: cú pháp, kiểm tra nội dung, diễn giải. Đó là mặc
định đúng đắn — hãy thu hẹp lại khi nào bạn có lý do đã đo đạc để làm vậy.

---

## 3. Đọc kết quả

`ParseResult.getAiObject()` giữ các AI đã giải mã. Hãy lấy một AI cụ thể theo mã của nó, đừng
theo vị trí:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Mỗi phần tử mang một danh sách **diễn giải** — phần ý nghĩa được mở ra đằng sau những chữ số
thô, do giai đoạn diễn giải tạo nên:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` đã được bản địa hóa và dành để hiển thị. Còn để *đọc* một giá trị trong mã, hãy
tra nó bằng khóa kiểu bất biến của nó:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Những AI khác nhau sinh ra những khóa khác nhau — GTIN cho ra tiền tố công ty, loại GTIN và
chữ số kiểm tra của nó; giá cho ra tiền tệ và số tiền thập phân. Danh sách đầy đủ nằm ở
[Phụ lục B](GaiaParser-Vietnamese.md#phụ-lục-b--hằng-số-khóa-diễn-giải), còn các hằng số thì ở
trong `GS1Constants_Enricher`. Không phải AI nào cũng có diễn giải: một số lô là văn bản tự
do, chẳng có gì để suy ra, nên danh sách của nó rỗng.

---

## 4. Xử lý một lần phân tích thất bại

Phần tải không hợp lệ là một kết quả bình thường, không phải ngoại lệ — `parse` không bao giờ
ném ngoại lệ vì dữ liệu GS1 hỏng:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Hãy rẽ nhánh theo `getId()`, đừng bao giờ theo thông báo.** Thông báo được bản địa hóa và
cách diễn đạt của chúng không phải một cam kết — hơn nữa hiện chúng còn mang một khiếm khuyết
đã biết về dấu nháy (phần `''` nhân đôi ở trên), được ghi nhận trong
[Tham chiếu lỗi](GaiaParser-Vietnamese.md#tham-chiếu-lỗi).

Hai câu hỏi khác nhau, hai phương thức khác nhau:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Việc phân tích ngừng đi sâu ngay khi một giai đoạn thất bại, nên một chữ số kiểm tra sai nghĩa
là bạn nhận được lỗi kiểm tra hợp lệ nhưng không có diễn giải nào.

### Cảnh báo không làm kết quả trở nên không hợp lệ

Một số phép kiểm tra chỉ mang tính khuyến cáo. Một tiền tố công ty GS1 không nhận ra được sẽ
được báo lại, nhưng phần tải vẫn đúng dạng:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Hãy dùng `getIssues()` khi bạn muốn cả hai cùng lúc. Nếu quy trình của bạn buộc phải từ chối
những tiền tố không nhận ra được, hãy kiểm tra `getWarnings()` một cách tường minh —
`isValid()` sẽ không làm điều đó thay bạn.

---

## 5. Hai điều sẽ làm bạn vấp

### Dấu phân tách GS, và vì sao bỏ sót nó còn tệ hơn một lỗi

Một AI độ dài thay đổi chạy tới một **ký tự GS** (ASCII `0x1D`, trong các hệ ký hiệu mã vạch
gọi là FNC1), hoặc tới hết chuỗi. Khi có một AI khác đi sau nó, dấu phân tách ấy là bắt buộc:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Bỏ sót nó thì bạn **không** nhận được lỗi — bạn nhận được một câu trả lời sai một cách đầy
tự tin:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` là `X..20`, nên việc nuốt trọn `LOT-ABC21SN-98765` là hoàn toàn hợp lý, và bộ phân
tích chẳng có cách nào biết đó không phải ý bạn. Về sau không gì cứu vãn được điều này, nên
hãy làm đúng dấu phân tách ngay từ nguồn: đọc byte từ máy quét theo **ISO-8859-1** để `0x1D`
sống sót, và viết `""` trong chuỗi ký tự hằng của Java. Các AI độ dài cố định (`01`, `17`,
`3103`) không cần dấu phân tách — bộ phân tích đã biết độ dài của chúng.

### Phần lớn AI không đứng một mình

Số lô, số sê-ri, ngày hết hạn và những thứ tương tự đều là *thuộc tính*: GS1 General
Specifications đòi hỏi chúng phải đi kèm một khóa nhận dạng, và Gaia thực thi điều đó.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Thêm một GTIN vào là qua. Nếu bạn thực sự cần phân tích một mảnh — một bài kiểm thử đơn vị,
một lần quét từng phần — hãy tắt phép kiểm tra ấy đi:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Tiền tố của máy quét và Digital Link đều chạy được ngay

Bạn không cần cho Gaia biết đầu vào ở dạng nào — nó nhận ra cả bốn dạng. Cứ đưa thẳng những
gì máy quét trả về.

**Tiền tố Mã nhận dạng hệ ký hiệu AIM** xác định hệ ký hiệu và được tách bỏ tự động:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**URI GS1 Digital Link** cũng đi qua đúng những bước kiểm tra hợp lệ và làm giàu ấy:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Vì cả hai dạng đều dẫn về cùng một `GS1AIObject`, đoạn mã tiêu thụ kết quả quét chẳng cần bận
tâm dạng nào đã đến — và `toElementString()` / `getCanonicalDigitalLink()` chuyển đổi qua lại
giữa chúng.

**Tiền tố tương quan 8 chữ số** (`12345678~…`) cũng được tách bỏ theo cách ấy và giữ lại
trong `getCorrelationInfo()`, nếu quy trình của bạn có dùng đến.

---

## 7. Làm ít việc hơn: các chế độ phân tích

Chế độ mặc định làm mọi thứ. Hãy yêu cầu ít hơn khi bạn chỉ cần một phần câu trả lời:

| Chế độ | Trả lời câu hỏi | Chi phí |
|---|---|---|
| `DATA_CARRIER` | Đây là hệ ký hiệu nào? | Rẻ nhất — không phân tích AI chút nào, `getAiObject()` là `null` |
| `SYNTAX` | Mã AI và độ dài có đúng dạng không? | Không chữ số kiểm tra, không diễn giải |
| `CONTENT` | Đây có phải dữ liệu GS1 hợp lệ không? | Kiểm tra đầy đủ, không diễn giải |
| `INTERPRETATION` | Nó có nghĩa là gì? | **Mặc định** — mọi thứ |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Hãy chọn `CONTENT` khi bạn kiểm tra hợp lệ với khối lượng lớn mà không bao giờ hiển thị phần
phân rã, và `DATA_CARRIER` khi bạn chỉ cần định tuyến kết quả quét tới đúng bộ xử lý.

---

## 8. Đổi ngôn ngữ và định dạng ngày

Thông báo lỗi, nhãn diễn giải và mô tả AI đều đã được dịch sang **35 ngôn ngữ**; ngày tháng
có thể hiển thị theo ý bạn. Tất cả gói gọn trong một `ParseConfig` bất biến duy nhất:

```java
ParseConfig config = ParseConfig.builder()
        .language(Language.FRENCH)
        .dateEndian(DateEndian.BIG)          // yyyy/mm/dd
        .dateSeparator(DateSeparator.HYPHEN) // yyyy-mm-dd
        .build();

ParseResult r = PARSER.parse("01095060001343521726123110LOT-001", config);

r.getAiObject().get("17").getDescription();
// "Date limite d'utilisation (AAMMJJ)"
```

```
Date                     2026-12-31
Format de date           yyyy-mm-dd
```

Giá trị thì không bao giờ được bản địa hóa — chỉ nhãn, mô tả và thông báo mà thôi — nên
`"2026-12-31"` và `"09506000134352"` mang cùng ý nghĩa trong mọi ngôn ngữ. Hãy dựng cấu hình
một lần lúc khởi động rồi dùng chung; nó là bất biến.

---

## 9. Dọn dẹp đầu vào lộn xộn

Nếu nguồn của bạn phát ra dấu ngoặc HRI đã in hay những khoảng trắng đi lạc, phần lõi có sẵn
hai **bộ sửa đổi đầu vào** để chỉnh lại phần tải trước khi phân tích:

```java
ParseConfig config = ParseConfig.builder()
        .modifier(new ModifierRemoveSpaces())        // register spaces first
        .modifier(new ModifierRemoveAIBrackets())
        .build();

ParseResult r = PARSER.parse("(01) 09506000134352 (17) 261231 (10) LOT-001", config);

r.isValid();                                     // true
r.getPayload();                                  // 01095060001343521726123110LOT-001
r.getModifierInfo().getAppliedModifiers();       // [Remove Space Characters, Remove Brackets Around AI]
```

Theo mặc định không bộ nào được bật, và cả hai đều kèm lưu ý — khoảng trắng lẫn dấu ngoặc đều
là ký tự dữ liệu GS1 hợp lệ, nên chỉ áp dụng chúng cho nguồn mà bạn đã biết rõ. Xem
[Các bộ sửa đổi có sẵn](GaiaParser-Vietnamese.md#các-bộ-sửa-đổi-có-sẵn), nơi cũng giải thích vì sao sau khi
bỏ dấu ngoặc thì phải khôi phục lại dấu phân tách mà chúng hàm ý.

---

## 10. Đi tiếp từ đâu

- **[Hướng dẫn cho lập trình viên GaiaParser](GaiaParser-Vietnamese.md)** — chi tiết về chuỗi xử lý, mô
  hình kết quả đầy đủ, mọi mã lỗi, cùng các phụ lục về AI và khóa diễn giải.
- **[Hướng dẫn cho lập trình viên GaiaBuilder](GaiaBuilder-Vietnamese.md)** — dựng chuỗi phần tử và URI
  Digital Link từ các cặp AI/giá trị.
- **[Tham chiếu HTTP của Gaia API](../../gaia-api-reference.md)** — vẫn bộ máy ấy nhưng qua HTTP,
  nếu bạn không muốn nhúng thư viện.
- **[ai-codes.txt](../../ai-codes.txt)** — một danh sách phẳng dạng `(AI) TITLE` để tra cứu nhanh.

### Bản năm dòng

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
