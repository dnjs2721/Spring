package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Auditing
 * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶으면?
 * 등록일, 수정일
 * 등록자, 수정자
 */

@MappedSuperclass
@Getter
public class JpaBaseEntity {
    @Column(updatable = false) // 생성 날짜가 변경되지 않게 update 를 막는다.
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // Persist 전에 동작
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
