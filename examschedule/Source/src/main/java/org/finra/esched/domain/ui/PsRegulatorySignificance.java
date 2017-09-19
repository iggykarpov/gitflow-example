package org.finra.esched.domain.ui;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;


@Entity
@Immutable
@Table (name="RGLTY_SGNFC_LK")
public class PsRegulatorySignificance {
    @Id
    @GeneratedValue
    @Column(name = "rglty_sgnfc_id")
    private Long id;
    @Column(name = "rglty_sgnfc_ds")
    private String description;

    public PsRegulatorySignificance() {
    	super();
    }
    
    @Override
    public String toString() {
        return "PsRegulatorySignificance{" +
                "id =" + id +
                ", description='" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PsRegulatorySignificance that = (PsRegulatorySignificance) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @JsonProperty("id")
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    @JsonProperty("description")
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



}
