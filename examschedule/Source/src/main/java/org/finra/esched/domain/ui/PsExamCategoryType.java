package org.finra.esched.domain.ui;

import org.codehaus.jackson.annotate.JsonProperty;
import org.finra.exam.common.domain.DomainObject;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Immutable
@Table (name="EXAM_CTGRY_LK")
public class PsExamCategoryType {
    @Id
    @GeneratedValue
    @Column(name = "EXAM_CTGRY_CD")
    private String code;
    @Column(name = "EXAM_CTGRY_DS")
    private String description;

 

    @Override
    public String toString() {
        return "ExamCategoryType{" +
                "code =" + code +
                ", description='" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PsExamCategoryType that = (PsExamCategoryType) o;

        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

}
