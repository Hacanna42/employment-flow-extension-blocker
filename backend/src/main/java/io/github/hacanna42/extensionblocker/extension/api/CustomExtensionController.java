package io.github.hacanna42.extensionblocker.extension.api;

import io.github.hacanna42.extensionblocker.extension.api.dto.AddCustomExtensionRequest;
import io.github.hacanna42.extensionblocker.extension.api.dto.CustomExtensionResponse;
import io.github.hacanna42.extensionblocker.extension.application.CustomExtensionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/extensions/custom")
public class CustomExtensionController {

    private final CustomExtensionService service;

    public CustomExtensionController(CustomExtensionService service) {
        this.service = service;
    }

    @GetMapping
    public List<CustomExtensionResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomExtensionResponse add(@Valid @RequestBody AddCustomExtensionRequest request) {
        return service.add(request.name());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        service.remove(id);
    }
}
