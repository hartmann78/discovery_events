package com.practice.events_service.generators;

import com.practice.events_service.dto.newDTO.NewCompilationDTO;
import com.practice.events_service.dto.updateRequest.UpdateCompilationRequest;
import com.practice.events_service.model.Compilation;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class CompilationGenerator {
    Random random = new Random();

    public Compilation generateCompilation() {
        return Compilation.builder()
                .title(generateName())
                .pinned(random.nextBoolean())
                .events(Collections.emptyList())
                .build();
    }

    public NewCompilationDTO generateNewCompilationDTO(List<Long> events) {
        return NewCompilationDTO.builder()
                .title(generateName())
                .pinned(true)
                .events(events)
                .build();
    }

    public UpdateCompilationRequest generateUpdateCompilationRequest(List<Long> events) {
        return UpdateCompilationRequest.builder()
                .title(generateName())
                .pinned(false)
                .events(events)
                .build();
    }

    private String generateName() {
        return RandomString.make(8);
    }
}
