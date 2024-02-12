-- 사용자
INSERT INTO USER (nickname, email, profile_image, agree_tos, agree_picu, role_type, created_at)
VALUES
    ('USER1', 'USER1@gmail.com', 'https://palworld.shwa.space/assets/PalIcon/T_PinkCat_icon_normal.png', true, true, 'ADMIN', CURRENT_TIMESTAMP),
    ('USER2', 'USER2@gmail.com', 'https://palworld.shwa.space/assets/PalIcon/T_PinkCat_icon_normal.png', true, true, 'USER', CURRENT_TIMESTAMP),
    ('USER3', 'USER3@gmail.com', 'https://palworld.shwa.space/assets/PalIcon/T_PinkCat_icon_normal.png', true, true, 'USER', CURRENT_TIMESTAMP),
    ('USER4', 'USER4@gmail.com', 'https://palworld.shwa.space/assets/PalIcon/T_PinkCat_icon_normal.png', true, true, 'USER', CURRENT_TIMESTAMP);
