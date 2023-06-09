package com.elephant.dreamhi.model.entity;

import com.elephant.dreamhi.model.dto.CastingSaveDto;
import com.elephant.dreamhi.model.dto.CastingUpdateDto;
import com.elephant.dreamhi.model.statics.Gender;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "casting")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Casting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer headcount;

    private Integer minHeight;

    private Integer maxHeight;

    private Integer minAge;

    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "casting", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CastingStyleRelation> castingStyleRelations = new ArrayList<>();

    public void addCastingStyleRelation(CastingStyleRelation castingStyleRelation) {
        this.castingStyleRelations.add(castingStyleRelation);
        if (castingStyleRelation.getCasting() != this) {
            castingStyleRelation.setCasting(this);
        }
    }

    public static Casting toEntity(Announcement announcement, CastingSaveDto castingSaveDto) {
        return Casting.builder()
                      .announcement(announcement)
                      .name(castingSaveDto.getName())
                      .description(castingSaveDto.getDescription())
                      .headcount(castingSaveDto.getHeadcount())
                      .minHeight(castingSaveDto.getMinHeight())
                      .maxHeight(castingSaveDto.getMaxHeight())
                      .minAge(castingSaveDto.getMinAge())
                      .maxAge(castingSaveDto.getMaxAge())
                      .gender(castingSaveDto.getGender())
                      .build();
    }

    public void changeCasting(CastingUpdateDto castingUpdateDto) {
        this.name = castingUpdateDto.getName();
        this.description = castingUpdateDto.getDescription();
        this.headcount = castingUpdateDto.getHeadcount();
        this.minHeight = castingUpdateDto.getMinHeight();
        this.maxHeight = castingUpdateDto.getMaxHeight();
        this.minAge = castingUpdateDto.getMinAge();
        this.maxAge = castingUpdateDto.getMaxAge();
        this.gender = castingUpdateDto.getGender();
    }

}
