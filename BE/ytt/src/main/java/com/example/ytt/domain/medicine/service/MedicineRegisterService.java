package com.example.ytt.domain.medicine.service;

import com.example.ytt.domain.medicine.domain.Ingredient;
import com.example.ytt.domain.medicine.domain.Medicine;
import com.example.ytt.domain.medicine.domain.MedicineIngredient;
import com.example.ytt.domain.medicine.dto.MedicineRequestDto;
import com.example.ytt.domain.medicine.repository.IngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineIngredientRepository;
import com.example.ytt.domain.medicine.repository.MedicineRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MedicineRegisterService {

    @Value("${open-api.url.base-url}")
    private String baseUrl;

    @Value("${open-api.url.dtl-url}")
    private String dtlUrl;

    @Value("${open-api.url.list-url}")
    private String listUrl;

    @Value("${open-api.serviceKey}")
    private String serviceKey;

    private final MedicineRepository medicineRepository;
    private final IngredientRepository ingredientRepository;
    private final MedicineIngredientRepository medicineIngredientRepository;

    private final ObjectMapper objectMapper;

    public boolean resisterMedicineByProductCode(MedicineRequestDto medicineRequestDto) {
        try {
            String imgUrl = jsonToImgUrl(getOpenApiData(listUrl, medicineRequestDto.productName(), "json"));
            List<MedicineIngredient> medicineIngredients = xmlToEntity(getOpenApiData(dtlUrl, medicineRequestDto.productName(), "xml"));

            Medicine medicine = medicineIngredients.get(0)
                    .getMedicine()
                    .setImageURL(imgUrl)
                    .setPrice(medicineRequestDto.productPrice());

            Medicine savedMedicine = medicineRepository.findByProductCode(medicine.getProductCode())
                    .orElseGet(() -> medicineRepository.save(medicine));

            List<MedicineIngredient> savedMedicineIngredients = medicineIngredients.stream()
                    .map(medicineIngredient -> {
                        Ingredient ingredient = medicineIngredient.getIngredient();
                        Ingredient savedIngredient = ingredientRepository.findByName(ingredient.getName())
                                .orElseGet(() -> ingredientRepository.save(ingredient));

                        return medicineIngredientRepository.save(MedicineIngredient.of(savedMedicine, savedIngredient, medicineIngredient.getQuantity(), medicineIngredient.getUnit()));
                    })
                    .toList();

            savedMedicineIngredients.forEach(medicineIngredient -> log.info(medicineIngredient.toString()));

            return true;
        } catch (IOException | NumberFormatException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean resisterMedicineByBarcode(String barcode, int price) {
        try {
            List<MedicineIngredient> medicineIngredients = xmlToEntity(getOpenApiDataByBarcode(dtlUrl, barcode, "xml"));

            Medicine medicine = medicineIngredients.get(0).getMedicine();

            String imgUrl = jsonToImgUrl(getOpenApiData(listUrl, medicine.getName(), "json"));

            medicine.setPrice(price);
            medicine.setImageURL(imgUrl);
            // 저장

            return false;
        }catch (IOException | NumberFormatException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Open API 데이터 조회 (약 정보)
     * @param productCode 약의 품목기준코드
     * @param type 조회 타입 (xml, json) - 상세 정보는  json 데이터로 받아와도 일부가 xml 형태이기에 xml 사용, 그외는 json 데이터로 받아옴
     * @return Open API 데이터
     * @throws IOException
     */
    public String getOpenApiData(String requestUrl, String productCode, String type) throws IOException {
        String urlStr = UriComponentsBuilder
                .fromHttpUrl(baseUrl + requestUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("item_name", productCode)
                .queryParam("type", type)
                .encode()
                .toUriString();

        URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        String content;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            content = br.lines().collect(Collectors.joining("\n"));
        }

        conn.disconnect();

        return content;
    }

    public String getOpenApiDataByBarcode(String requestUrl, String barcode, String type) throws IOException {
        String urlStr = UriComponentsBuilder
                .fromHttpUrl(baseUrl + requestUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("bar_code", barcode)
                .queryParam("type", type)
                .encode()
                .toUriString();

        URL url = new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        String content;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            content = br.lines().collect(Collectors.joining("\n"));
        }

        conn.disconnect();

        return content;
    }

    private String jsonToImgUrl(String data) throws IOException {
        // JSON 데이터 파싱
        Map<String, Object> parsedData = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> body = (Map<String, Object>) parsedData.get("body");

        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        return (String) items.get(0).get("BIG_PRDT_IMG_URL");
    }

    private List<MedicineIngredient> xmlToEntity(String data) throws NumberFormatException {
        Document doc = Jsoup.parse(data, "", org.jsoup.parser.Parser.xmlParser());
        Element item = doc.select("item").first();

        List<MedicineIngredient> medicineIngredients = new ArrayList<>();

        Medicine medicine = Medicine.builder()
                .name(item.select("ITEM_NAME").text())                  // 품목명
                .productCode(item.select("ITEM_SEQ").text())            // 품목기준코드
                .manufacturer(item.select("ENTP_NAME").text())          // 업체명
                .efficacy(parseDocData(item.select("EE_DOC_DATA")))     // 효능효과
                .usages(parseDocData(item.select("UD_DOC_DATA")))        // 용법용량
                .precautions(parseDocData(item.select("NB_DOC_DATA")))  // 주의사항
                .validityPeriod(item.select("VALID_TERM").text())       // 유효기간
                .imageURL(null)                                                 // 이미지 URL은 추가로 기져와야 함
                .price(1000)                                                    // 가격은 일단 임의로 설정
                .build();
//        item.select("BAR_CODE").text(); // 표준코드(barcode) 필요하면 추가

        // MATERIAL_NAME 분할 코드는 추후에 정리
        String[] components = item.select("MATERIAL_NAME").text().split(";");

        for (String component : components) {
            String[] fields = component.split("\\|");
            String[] arr = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                String[] keyValue = fields[i].split(" : ");
                arr[i] = (keyValue.length == 2) ? keyValue[1].trim() : null;
            }

            Ingredient ingredient = Ingredient.of(arr[1], arr[5], arr[4]);
            medicineIngredients.add(MedicineIngredient.of(medicine, ingredient, Double.parseDouble(arr[2]), arr[3]));
        }

        return medicineIngredients;
    }

    private String parseDocData(Elements docData) {
        Elements articles = docData.select("ARTICLE");
        StringBuilder result = new StringBuilder();

        for (Element article : articles) {
            String title = article.attr("title");
            Elements paragraphs = article.select("PARAGRAPH");

            if (!title.isEmpty()) {
                result.append(title).append("\n");
            }

            paragraphs.forEach(paragraph -> result.append(paragraph.text()).append("\n"));

            result.append("\n");
        }

        // 마지막 줄바꿈 제거
        result.delete(result.length() - 2, result.length());

        return result.toString();
    }

}


