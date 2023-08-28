# SQL Cleaner Plugin

The SQL Cleaner Plugin is a tool designed to assist developers in formatting SQL queries within SQL files.

## Features
The SQL Cleaner Plugin offers two main actions:

1. Format All SQL in File
   This action allows you to format all SQL queries within the currently opened file. 
   By selecting this option from the editor menu, 
   the plugin will automatically scan the entire file, identify SQL queries, 
   and format them according to a standardized style. 
   **Here's how it works:**
   - By right-clicking on the SQ file, you will have the option to ``Format All SQL``

2. Format Specific SQL Query
   Format the particular SQL query, preserving the rest of the content in the file. 
   **Here's how it works:**
    - When you hover your cursor over a specific SQL query 
      within the editor, the plugin will detect the hovered SQL query and highlight it.
    - By right-clicking on the highlighted SQL query 
       you will have the option to ``Format Selected SQL``

### Supported SQL

``WORKS ONLY WITH VALID SQL QUERIES``

- DELETE
- INSERT
- CREATE TABLE