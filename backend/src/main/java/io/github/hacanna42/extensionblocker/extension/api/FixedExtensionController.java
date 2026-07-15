package io.github.hacanna42.extensionblocker.extension.api;

import io.github.hacanna42.extensionblocker.extension.api.dto.FixedExtensionResponse;
import io.github.hacanna42.extensionblocker.extension.api.dto.UpdateFixedExtensionRequest;
import io.github.hacanna42.extensionblocker.extension.application.FixedExtensionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extensions/fixed")
public class FixedExtensionController {

    private final FixedExtensionService service;

    public FixedExtensionController(FixedExtensionService service) {
        this.service = service;
    }

    @GetMapping
    public List<FixedExtensionResponse> list() {
        return service.list();
    }

    @PatchMapping("/{name}")
    public FixedExtensionResponse update(@PathVariable String name, @Valid @RequestBody UpdateFixedExtensionRequest request) {
        return service.updateChecked(name, request.checked());
    }
}
