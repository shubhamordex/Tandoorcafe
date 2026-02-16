# Tandoor Night Cafe - Android POS Application

A full-featured Point of Sale (POS) Android application for Tandoor Night Cafe, built with Java and SQLite.

## Features

### Core Functionality
- **Menu Management**: Add, edit, and delete menu items with categories (Appetizer, Main Course, Bread, Beverage, Dessert)
- **Order Management**: Create orders with cart functionality, category filtering, and quantity adjustment
- **Billing & Invoicing**: Generate invoices with customer details, payment method selection, and PDF export
- **Order History**: View all past orders with detailed information
- **Sales Reports**: Generate date-range reports with CSV export capability
- **Settings**: Configure tax rate, restaurant information, and language preferences

### Technical Features
- **Bilingual Support**: English and Hindi UI with runtime language switching
- **SQLite Database**: Local data persistence with SQLiteOpenHelper
- **Material Design**: Modern UI with Material Components
- **Responsive Layouts**: Tablet support with layout-sw600dp variants
- **PDF Generation**: Invoice PDF creation using iText library
- **CSV Export**: Sales report export functionality
- **Input Validation**: Comprehensive validation for all forms
- **MVC Architecture**: Clean separation of concerns

## Technical Stack

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Database**: SQLite with SQLiteOpenHelper
- **UI**: Material Components, RecyclerView, CardView
- **Libraries**:
  - AndroidX AppCompat 1.6.1
  - Material Components 1.11.0
  - RecyclerView 1.3.2
  - CardView 1.0.0
  - iText PDF 5.5.10

## Project Structure

```
app/src/main/
├── java/com/tandoornightcafe/app/
│   ├── adapter/           # RecyclerView adapters
│   │   ├── CartAdapter.java
│   │   ├── MenuAdapter.java
│   │   ├── OrderAdapter.java
│   │   └── SelectMenuAdapter.java
│   ├── db/                # Database layer
│   │   └── DBHelper.java
│   ├── model/             # Data models
│   │   ├── CartItem.java
│   │   ├── MenuItem.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── ui/                # Activities
│   │   ├── BillingActivity.java
│   │   ├── HomeActivity.java
│   │   ├── MenuActivity.java
│   │   ├── OrderActivity.java
│   │   ├── OrdersListActivity.java
│   │   ├── ReportsActivity.java
│   │   ├── SettingsActivity.java
│   │   └── SplashActivity.java
│   └── util/              # Utility classes
│       ├── CSVExporter.java
│       ├── LocaleHelper.java
│       └── PDFGenerator.java
└── res/
    ├── layout/            # Phone layouts
    ├── layout-sw600dp/    # Tablet layouts
    ├── values/            # English strings & resources
    ├── values-hi/         # Hindi strings
    └── values-sw600dp/    # Tablet dimensions
```

## Database Schema

### Tables

1. **menu_items**
   - id (PRIMARY KEY)
   - name
   - description
   - category
   - price

2. **orders**
   - id (PRIMARY KEY)
   - customer_name
   - customer_phone
   - subtotal
   - tax
   - total
   - payment_method
   - status
   - order_date
   - invoice_number (UNIQUE)

3. **order_items**
   - id (PRIMARY KEY)
   - order_id (FOREIGN KEY)
   - menu_item_id
   - item_name
   - quantity
   - price
   - subtotal

4. **settings**
   - key (PRIMARY KEY)
   - value

## Setup & Build

1. Clone the repository
2. Open project in Android Studio
3. Sync Gradle dependencies
4. Run on emulator or physical device (API 24+)

```bash
./gradlew build
./gradlew installDebug
```

## Usage

### First Launch
1. App starts with splash screen
2. Navigate to Settings to configure:
   - Restaurant name, address, phone
   - Tax rate
   - Language preference (English/Hindi)

### Managing Menu
1. Click "Menu Management" on home screen
2. Use FAB (+) to add new items
3. Click edit/delete icons on items to modify

### Creating Orders
1. Click "New Order" on home screen
2. Browse menu by category
3. Add items to cart
4. Adjust quantities as needed
5. Click "Proceed to Billing"

### Billing
1. Enter customer details
2. Select payment method
3. Click "Generate Invoice"
4. Save PDF or share invoice

### Reports
1. Click "Reports" on home screen
2. Select date range
3. Generate report to view statistics
4. Export to CSV for external analysis

## Language Support

The app supports English and Hindi with complete translations. Change language in Settings and restart the app to apply.

- English: Default
- Hindi: हिंदी (full UI translation)

## Permissions

- **WRITE_EXTERNAL_STORAGE**: For PDF and CSV export (Android 9 and below)
- **READ_EXTERNAL_STORAGE**: For file access (Android 12 and below)

Note: Android 10+ uses scoped storage, no permissions needed for app-specific directories.

## License

Proprietary - All rights reserved by Tandoor Night Cafe

## Version

1.0 - Initial Release
