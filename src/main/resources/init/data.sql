use webflux;

insert into users(login_id, login_pw, name, email, enabled, role)
values
    ('user1', 'vWdYoyWQpSM64FP7jbIZ/rYO79Sx9rxgvBMjQ1duMQs=', '유저1', 'test1@test.com', true, 'ADMIN'),
    ('user2', 'vWdYoyWQpSM64FP7jbIZ/rYO79Sx9rxgvBMjQ1duMQs=', '유저2', 'test2@test.com', false, 'USER');


insert into todos(user_cid, title, content, created_at, updated_at, done)
values
    (1, '오늘할일1', '작업량많음', now(), now(), false),
    (1, '오늘할일2', '귀찮음', now(), now(), false),
    (1, '오늘할일3', '작업량적음', now(), now(), true);