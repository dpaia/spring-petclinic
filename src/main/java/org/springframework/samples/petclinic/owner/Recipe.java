package org.springframework.samples.petclinic.owner;

import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.util.Objects;

@Entity
@Table(name = "recipe")
public class Recipe extends BaseEntity {

	@Column(name = "text")
	private String text;

	@ManyToOne
	@JoinColumn(name = "visit_id")
	private Visit visit;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		Recipe recipe = (Recipe) o;
		return getId() != null && Objects.equals(getId(), recipe.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}
