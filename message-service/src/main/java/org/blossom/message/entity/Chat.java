package org.blossom.message.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.blossom.message.enums.ChatType;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Blossom_Chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Blossom_Chat_Participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> participants;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_type")
    private ChatType chatType;

    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "last_update")
    private Date lastUpdate;

    public void addToChat(User user) {
        this.participants.add(user);
    }

    public void removeFromChat(User oldUser) {
        this.participants.removeIf(user -> user.getId() == oldUser.getId());
        if (this.owner.getId() == oldUser.getId()) {
            this.owner = null;
        }
    }

    public void setNewOwner(User newOwner) {
        this.owner = newOwner;
    }
}