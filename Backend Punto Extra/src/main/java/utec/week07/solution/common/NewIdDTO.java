package utec.week07.solution.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class NewIdDTO {
    private Long id;

    public NewIdDTO(Long id) {
        this.id = id;
    }
}
