package org.finra.esched.domain.ui;

import org.codehaus.jackson.annotate.JsonProperty;
import org.finra.exam.common.domain.DomainObject;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Immutable
@Table (name="EXAM_SUB_TYPE_LK")
public class PsExamSubTypeType {
    @Id
    @GeneratedValue
    @Column(name = "EXAM_SUB_TYPE_ID")
    private String codeId;
    @Column(name = "EXAM_SUB_TYPE_CD")
    private String code;
    @Column(name = "EXAM_SUB_TYPE_DS")
    private String description;
    @Column(name = "EXAM_TYPE_CD")
    private String examType;

    public PsExamSubTypeType() {
    	super();
    }
    
    @Override
    public String toString() {
        return "ExamSubTypeType{" +
                "code =" + code +
                ", description='" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PsExamSubTypeType that = (PsExamSubTypeType) o;

        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @JsonProperty("codeId")
    public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
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

    @JsonProperty("examType")
    public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

}
