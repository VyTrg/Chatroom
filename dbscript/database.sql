USE [chatroom]
GO
/****** Object:  Table [dbo].[attachment]    Script Date: 6/3/2025 9:47:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[attachment](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [message_id] [bigint] NOT NULL,
    [upload_at] [datetime2](6) NOT NULL,
    [file_type] [varchar](255) NOT NULL,
    [file_url] [varchar](255) NOT NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[block]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[block](
    [blocked_id] [bigint] NOT NULL,
    [blocker_id] [bigint] NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [UK_Block] UNIQUE NONCLUSTERED
(
    [blocker_id] ASC,
[blocked_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[contact_request]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[contact_request](
    [is_accepted] [bit] NULL,
    [created_at] [datetime2](6) NOT NULL,
    [delete_at] [datetime2](6) NULL,
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [receiver_id] [bigint] NULL,
    [sender_id] [bigint] NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[contact_with]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[contact_with](
    [contact_one_id] [bigint] NOT NULL,
    [contact_two_id] [bigint] NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [deleted_at] [datetime2](6) NULL,
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[contact_with_controller]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[contact_with_controller](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [deleted_at] [datetime2](6) NULL,
    [contact_one_id] [bigint] NOT NULL,
    [contact_two_id] [bigint] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[conversation]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[conversation](
    [is_group] [bit] NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [image_url] [varchar](255) NULL,
    [name] [varchar](255) NOT NULL,
    [deleted_at] [datetime2](6) NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[conversation_member]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[conversation_member](
    [conversation_id] [bigint] NULL,
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [joined_at] [datetime2](6) NOT NULL,
    [user_id] [bigint] NULL,
    [role] [varchar](255) NOT NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[message]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[message](
    [is_read] [bit] NOT NULL,
    [conversation_id] [bigint] NULL,
    [created_at] [datetime2](6) NOT NULL,
    [deleted_at] [datetime2](6) NULL,
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [updated_at] [datetime2](6) NULL,
    [user_id] [bigint] NULL,
    [message_text] [varchar](255) NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[user]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[user](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [first_name] [varchar](255) NOT NULL,
    [last_name] [varchar](255) NOT NULL,
    [username] [varchar](255) NOT NULL,
    [hash_password] [varchar](255) NOT NULL,
    [email] [varchar](255) NOT NULL,
    [status] [bit] NOT NULL,
    [last_seen] [datetime2](6) NOT NULL,
    [profile_picture] [varchar](255) NULL,
    [enabled] [bit] NOT NULL,
    CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[verification_token]    Script Date: 6/3/2025 9:47:02 PM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[verification_token](
    [expiry_date] [datetime2](6) NULL,
    [id] [bigint] NOT NULL,
    [user_id] [bigint] NOT NULL,
    [token] [varchar](255) NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [UKq6jibbenp7o9v6tq178xg88hg] UNIQUE NONCLUSTERED
(
[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
ALTER TABLE [dbo].[attachment]  WITH CHECK ADD  CONSTRAINT [FKoo11928qbsiolkc10dph1p214] FOREIGN KEY([message_id])
    REFERENCES [dbo].[message] ([id])
    GO
ALTER TABLE [dbo].[attachment] CHECK CONSTRAINT [FKoo11928qbsiolkc10dph1p214]
    GO
ALTER TABLE [dbo].[block]  WITH CHECK ADD  CONSTRAINT [FK2u8bu2wsm8obrs4ycqjrdpowi] FOREIGN KEY([blocker_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[block] CHECK CONSTRAINT [FK2u8bu2wsm8obrs4ycqjrdpowi]
    GO
ALTER TABLE [dbo].[block]  WITH CHECK ADD  CONSTRAINT [FK9lktn0a743asi5k1rffbisr7r] FOREIGN KEY([blocked_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[block] CHECK CONSTRAINT [FK9lktn0a743asi5k1rffbisr7r]
    GO
ALTER TABLE [dbo].[contact_request]  WITH CHECK ADD  CONSTRAINT [FK358gryht8tht6b1r26wcus5ox] FOREIGN KEY([sender_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[contact_request] CHECK CONSTRAINT [FK358gryht8tht6b1r26wcus5ox]
    GO
ALTER TABLE [dbo].[contact_request]  WITH CHECK ADD  CONSTRAINT [FK9pkd358ykpx6dny2r1lc918sl] FOREIGN KEY([receiver_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[contact_request] CHECK CONSTRAINT [FK9pkd358ykpx6dny2r1lc918sl]
    GO
ALTER TABLE [dbo].[contact_with]  WITH CHECK ADD  CONSTRAINT [FKe5yfp52kegdqf5avv1t47bhp0] FOREIGN KEY([contact_one_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[contact_with] CHECK CONSTRAINT [FKe5yfp52kegdqf5avv1t47bhp0]
    GO
ALTER TABLE [dbo].[contact_with]  WITH CHECK ADD  CONSTRAINT [FKplse661p66la0p5l610hu52hy] FOREIGN KEY([contact_two_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[contact_with] CHECK CONSTRAINT [FKplse661p66la0p5l610hu52hy]
    GO
ALTER TABLE [dbo].[contact_with_controller]  WITH CHECK ADD  CONSTRAINT [FK6cgoekq5yieo808daak7se8wo] FOREIGN KEY([contact_two_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[contact_with_controller] CHECK CONSTRAINT [FK6cgoekq5yieo808daak7se8wo]
    GO
ALTER TABLE [dbo].[contact_with_controller]  WITH CHECK ADD  CONSTRAINT [FKsoiagis2jgtwp6ly0kit8l6ft] FOREIGN KEY([contact_one_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[contact_with_controller] CHECK CONSTRAINT [FKsoiagis2jgtwp6ly0kit8l6ft]
    GO
ALTER TABLE [dbo].[conversation_member]  WITH CHECK ADD  CONSTRAINT [FKej5b0m23mn0y4xoysomelkriw] FOREIGN KEY([user_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[conversation_member] CHECK CONSTRAINT [FKej5b0m23mn0y4xoysomelkriw]
    GO
ALTER TABLE [dbo].[conversation_member]  WITH CHECK ADD  CONSTRAINT [FKg4mccr2hoe0jpr192dolgj4ks] FOREIGN KEY([conversation_id])
    REFERENCES [dbo].[conversation] ([id])
    GO
ALTER TABLE [dbo].[conversation_member] CHECK CONSTRAINT [FKg4mccr2hoe0jpr192dolgj4ks]
    GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FK6yskk3hxw5sklwgi25y6d5u1l] FOREIGN KEY([conversation_id])
    REFERENCES [dbo].[conversation] ([id])
    GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FK6yskk3hxw5sklwgi25y6d5u1l]
    GO
ALTER TABLE [dbo].[message]  WITH CHECK ADD  CONSTRAINT [FKnebwitbhvl9nq6mqsdlmb0v75] FOREIGN KEY([user_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[message] CHECK CONSTRAINT [FKnebwitbhvl9nq6mqsdlmb0v75]
    GO
ALTER TABLE [dbo].[verification_token]  WITH CHECK ADD  CONSTRAINT [FK6fi831ey1xa2o3dmkq5pgvkib] FOREIGN KEY([user_id])
    REFERENCES [dbo].[user] ([id])
    GO
ALTER TABLE [dbo].[verification_token] CHECK CONSTRAINT [FK6fi831ey1xa2o3dmkq5pgvkib]
    GO
ALTER TABLE [dbo].[conversation_member]  WITH CHECK ADD CHECK  (([role]='member' OR [role]='admin'))
    GO
