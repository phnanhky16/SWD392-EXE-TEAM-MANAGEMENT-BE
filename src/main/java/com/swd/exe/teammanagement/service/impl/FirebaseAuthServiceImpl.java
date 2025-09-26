package com.swd.exe.teammanagement.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swd.exe.teammanagement.service.FirebaseAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    @Override
    public FirebaseUserInfo verify(String idToken) throws FirebaseAuthException {
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return new FirebaseUserInfo(decoded.getUid(), decoded.getEmail(), decoded.getName(), decoded.getPicture());
    }
}
