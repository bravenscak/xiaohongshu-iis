package hr.algebra.xiaohongshuiis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.*;

@Entity
@Table(name = "users")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @Id
    @XmlElement
    private String id;

    @NotBlank
    @XmlElement
    private String name;

    @NotBlank
    @Column(name = "red_id")
    @XmlElement(name = "redId")
    private String redId;

    @Column(name = "followers_count")
    @XmlElement(name = "followersCount")
    private Integer followersCount;

    @Column(name = "notes_count")
    @XmlElement(name = "notesCount")
    private Integer notesCount;

    @XmlElement
    private Boolean verified;

    public User() {}

    public User(String id, String name, String redId) {
        this.id = id;
        this.name = name;
        this.redId = redId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRedId() { return redId; }
    public void setRedId(String redId) { this.redId = redId; }

    public Integer getFollowersCount() { return followersCount; }
    public void setFollowersCount(Integer followersCount) { this.followersCount = followersCount; }

    public Integer getNotesCount() { return notesCount; }
    public void setNotesCount(Integer notesCount) { this.notesCount = notesCount; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}