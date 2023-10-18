package com.github.alenfive.datajellyfish.security;

import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProfileService {

    @Value("${pac4j.properties.usernameAttribute:}")
    private String usernameAttribute;

    @Value("${pac4j.properties.nicknameAttribute:}")
    private String nicknameAttribute;



    @Autowired
    private ProfileManager profileManager;


    public Profile getSessionProfile() {

        UserProfile userProfile = profileManager.getProfile().orElse(null);

        if (userProfile == null || "AnonymousClient".equals(userProfile.getClientName())){
            return Profile.builder()
                    .username("Guest")
                    .nickname("Guest")
                    .build();
        }

        if (!StringUtils.hasText(usernameAttribute)){
            throw new RuntimeException("未配置属性:pac4j.properties.usernameAttribute");
        }

        if (!StringUtils.hasText(nicknameAttribute)){
            throw new RuntimeException("未配置属性:pac4j.properties.nicknameAttribute");
        }

        Object attrValue = userProfile.getAttribute(this.usernameAttribute);
        String username = null == attrValue ? null : String.valueOf(attrValue);

        attrValue = userProfile.getAttribute(this.nicknameAttribute);
        String nickName = null == attrValue ? null : String.valueOf(attrValue);

        return Profile.builder()
                .username(username)
                .nickname(nickName)
                .build();
    }
}
