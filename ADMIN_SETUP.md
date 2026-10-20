# CampusVoice Admin Roles (first implementation)

## Roles

- **HOD**: full access to every category and the Admin Management page.
- **Category admin**: can only view, update, view history for, or delete suggestions in their assigned category.
- One active category admin is allowed per category in this implementation.

## First startup / HOD account

The application creates the first HOD account in the SQLite database if no HOD account exists. It uses the existing environment variables:

- `CAMPUSVOICE_ADMIN_USERNAME`
- `CAMPUSVOICE_ADMIN_PASSWORD`

The HOD display name is `Rais Mulla`; the login username and password are whatever values you configured in those environment variables. The password is stored as a salted PBKDF2-HMAC-SHA256 hash, not as plain text.

Set those environment variables before starting the application. Do not commit passwords, `.env` files, or the SQLite database to GitHub.

## Creating category admins

1. Log in as the HOD.
2. Open **Manage Admins** in the dashboard header.
3. Enter the admin's display name, unique username, password (minimum 10 characters), and category.
4. Share their login details securely.
5. Use the table to change their display name/category or delete their account.

An active category cannot be assigned to a second category admin. Reassign or delete the existing admin first.

## Existing local database

This source package intentionally does not include `campusvoice.db`, because it can contain private student submissions. Back up your current database before updating source files. Keeping your existing database preserves existing suggestions; Hibernate will add the `admin_accounts` table on startup.

The database is still local SQLite at this stage. Cloud database and deployment are a separate later task.
