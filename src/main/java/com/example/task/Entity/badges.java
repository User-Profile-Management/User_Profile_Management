package com.example.task.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class badges {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int badge_id;
    public String badge_name;

}
