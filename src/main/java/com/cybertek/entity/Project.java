package com.cybertek.entity;

import com.cybertek.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="projects")
@Where(clause = "is_deleted=false")
public class Project extends  BaseEntity{

    @Column(unique = true) // server side validation
    private  String projectCode;
    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="manager_id")
    private User assignedManager;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String projectDetail;
    @Enumerated(EnumType.STRING)
    private Status projectStatus;

    private int completeTaskCounts;
    private int unfinishedTaskCounts;

}
