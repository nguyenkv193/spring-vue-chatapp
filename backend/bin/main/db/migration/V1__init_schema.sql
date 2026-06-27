create extension if not exists pgcrypto;

create table if not exists users (
    id uuid primary key default gen_random_uuid(),
    email varchar(255) not null unique,
    password_hash varchar(255),
    display_name varchar(120) not null,
    avatar_url varchar(512),
    active boolean not null default true,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists conversations (
    id uuid primary key default gen_random_uuid(),
    title varchar(160),
    type varchar(20) not null,
    created_by uuid references users(id),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists conversation_members (
    id uuid primary key default gen_random_uuid(),
    conversation_id uuid not null references conversations(id) on delete cascade,
    user_id uuid not null references users(id) on delete cascade,
    role varchar(20) not null,
    joined_at timestamptz not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint uk_conversation_member unique (conversation_id, user_id)
);

create table if not exists messages (
    id uuid primary key default gen_random_uuid(),
    conversation_id uuid not null references conversations(id) on delete cascade,
    sender_id uuid references users(id) on delete set null,
    content text not null,
    message_type varchar(20) not null,
    sent_at timestamptz not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_conversation_members_user_id on conversation_members (user_id);
create index if not exists idx_messages_conversation_sent_at on messages (conversation_id, sent_at);
create index if not exists idx_messages_sender_id on messages (sender_id);
