package com.pacgem.pdfservice.service;

import com.pacgem.pdfservice.model.dto.StickerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TemplateService {

    // Temporary static map for demo (can later be replaced by DB/S3 loading)
    private final Map<String, StickerTemplate> templateStore = new ConcurrentHashMap<>();

    public TemplateService() {
        // Pre-load basic templates
        templateStore.put("DEFAULT", new StickerTemplate("DEFAULT", "templates/default-template.pdf", 595, 842, false, "Helvetica", 12));
        templateStore.put("LABEL_A5", new StickerTemplate("LABEL_A5", "templates/label-a5-template.pdf", 420, 595, false, "Courier", 10));
    }

    @Cacheable(value = "templates", key = "#format")
    public StickerTemplate getTemplate(String format) {
        if (!templateStore.containsKey(format)) {
            throw new IllegalArgumentException("Unknown template format: " + format);
        }
        return templateStore.get(format);
    }

    // Optional: clear cache when templates are updated
    // @CacheEvict(value = "templates", allEntries = true)
}
