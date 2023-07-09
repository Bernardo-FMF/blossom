package org.blossom.auth.delta.markable;

import lombok.Data;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.User;

@Data
public class UserMarkable implements EntityMarkable<User> {
    private boolean markedImageUrl;
    private boolean markedResetPasswordToken;
    private boolean markedPassword;

    private User delegate = User.builder().build();

    public UserMarkable markPassword(String newPassword) {
        this.markedPassword = true;
        delegate.setPassword(newPassword);
        return this;
    }

    public UserMarkable markImageUrl(String newImageUrl) {
        this.markedImageUrl = true;
        delegate.setImageUrl(newImageUrl);
        return this;
    }

    public UserMarkable markResetPasswordToken(PasswordReset passwordReset) {
        this.markedResetPasswordToken = true;
        delegate.setPasswordResetToken(passwordReset);
        return this;
    }

    @Override
    public User getDelegate() {
        return delegate;
    }
}
