package com.safecore.mobile.security;

import java.util.UUID;

/** Principal autenticado extraído do JWT (claim uid + subject email + claim perfil). */
public record AuthenticatedUser(UUID uid, String email, String perfil) {
}
