package com.swd.exe.teammanagement.service;

import com.google.firebase.auth.FirebaseAuthException;

public interface FirebaseAuthService {
    FirebaseUserInfo verify(String idToken) throws FirebaseAuthException;

    record FirebaseUserInfo(
            String uid,
            String email,
            String name,
            String pictureUrl
    ) {}
}
