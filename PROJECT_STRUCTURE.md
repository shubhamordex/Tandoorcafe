# Tandoor Night Cafe - Project Structure

## Overview
Complete Android POS application built from scratch with Java, SQLite, and Material Design.

## Statistics
- **Total Files**: 63
- **Java Source Files**: 20
- **XML Resource Files**: 39
- **Build Configuration Files**: 4

## File Structure

### Root Configuration
```
.gitignore
README.md
settings.gradle
build.gradle
gradle.properties
gradle/wrapper/gradle-wrapper.properties
```

### Application Module
```
app/
├── .gitignore
├── build.gradle
├── proguard-rules.pro
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/tandoornightcafe/app/
    │   ├── adapter/
    │   │   ├── CartAdapter.java
    │   │   ├── MenuAdapter.java
    │   │   ├── OrderAdapter.java
    │   │   └── SelectMenuAdapter.java
    │   ├── db/
    │   │   └── DBHelper.java
    │   ├── model/
    │   │   ├── CartItem.java
    │   │   ├── MenuItem.java
    │   │   ├── Order.java
    │   │   └── OrderItem.java
    │   ├── ui/
    │   │   ├── BillingActivity.java
    │   │   ├── HomeActivity.java
    │   │   ├── MenuActivity.java
    │   │   ├── OrderActivity.java
    │   │   ├── OrdersListActivity.java
    │   │   ├── ReportsActivity.java
    │   │   ├── SettingsActivity.java
    │   │   └── SplashActivity.java
    │   └── util/
    │       ├── CSVExporter.java
    │       ├── LocaleHelper.java
    │       └── PDFGenerator.java
    └── res/
        ├── drawable/
        │   ├── button_background.xml
        │   ├── ic_add.xml
        │   ├── ic_menu.xml
        │   └── splash_background.xml
        ├── layout/
        │   ├── activity_billing.xml
        │   ├── activity_home.xml
        │   ├── activity_menu.xml
        │   ├── activity_order.xml
        │   ├── activity_orders_list.xml
        │   ├── activity_reports.xml
        │   ├── activity_settings.xml
        │   ├── activity_splash.xml
        │   ├── dialog_menu_item.xml
        │   ├── dialog_order_details.xml
        │   ├── item_cart.xml
        │   ├── item_menu.xml
        │   ├── item_order.xml
        │   └── item_select_menu.xml
        ├── layout-sw600dp/
        │   └── activity_home.xml (tablet variant)
        ├── mipmap-anydpi-v26/
        │   ├── ic_launcher.xml
        │   └── ic_launcher_round.xml
        ├── mipmap-hdpi/
        │   └── ic_launcher_foreground.xml
        ├── mipmap-mdpi/
        │   └── ic_launcher_foreground.xml
        ├── mipmap-xhdpi/
        │   └── ic_launcher_foreground.xml
        ├── mipmap-xxhdpi/
        │   └── ic_launcher_foreground.xml
        ├── mipmap-xxxhdpi/
        │   └── ic_launcher_foreground.xml
        ├── values/
        │   ├── colors.xml
        │   ├── dimens.xml
        │   ├── strings.xml (English)
        │   └── themes.xml
        ├── values-hi/
        │   └── strings.xml (Hindi)
        ├── values-sw600dp/
        │   └── dimens.xml (tablet dimensions)
        └── xml/
            └── file_paths.xml
```

## Key Components

### Data Layer
1. **DBHelper.java** - SQLite database management with 4 tables:
   - menu_items: Item catalog
   - orders: Order headers
   - order_items: Order line items
   - settings: App configuration

### Model Layer
1. **MenuItem.java** - Menu item entity
2. **Order.java** - Order header entity
3. **OrderItem.java** - Order line item entity
4. **CartItem.java** - Shopping cart item (transient)

### View Layer (Activities)
1. **SplashActivity** - Launch screen with branding
2. **HomeActivity** - Main dashboard with feature cards
3. **MenuActivity** - Menu CRUD with RecyclerView
4. **OrderActivity** - Two-pane order creation (menu + cart)
5. **BillingActivity** - Customer details and invoice generation
6. **OrdersListActivity** - Order history with detail dialog
7. **ReportsActivity** - Sales analytics with date range
8. **SettingsActivity** - App configuration and language toggle

### Adapter Layer
1. **MenuAdapter** - Menu items with edit/delete actions
2. **SelectMenuAdapter** - Menu items for order selection
3. **CartAdapter** - Cart items with quantity controls
4. **OrderAdapter** - Order history items

### Utility Layer
1. **PDFGenerator** - Invoice PDF creation with iText
2. **CSVExporter** - Sales report CSV export
3. **LocaleHelper** - Runtime language switching

## Resource Organization

### Layouts
- **Phone layouts**: 14 XML files in `layout/`
- **Tablet layouts**: 1 optimized layout in `layout-sw600dp/`
- **Dialogs**: 2 reusable dialog layouts

### Strings
- **English**: Complete string resources in `values/strings.xml`
- **Hindi**: Full translation in `values-hi/strings.xml`
- **Total strings**: 100+ localized entries

### Themes
- Material Design 3 base theme
- Custom color palette (primary orange/accent)
- Consistent component styling

## Features Implemented

### Core Functionality
- [x] Menu CRUD operations
- [x] Order creation with cart
- [x] Tax calculation (configurable rate)
- [x] Invoice generation with PDF export
- [x] Order history with search
- [x] Sales reports with date filtering
- [x] CSV export for reports
- [x] Settings management

### UI/UX
- [x] Material Design components
- [x] Responsive layouts (phone + tablet)
- [x] Bilingual support (English + Hindi)
- [x] Input validation
- [x] Error handling
- [x] Loading states
- [x] Empty states

### Technical
- [x] SQLite database with proper schema
- [x] MVC architecture
- [x] File provider for sharing
- [x] Locale management
- [x] Date/time formatting
- [x] Currency formatting
- [x] PDF generation
- [x] CSV generation

## Build Configuration

### Gradle Files
- **Project build.gradle**: AGP 8.2.0
- **App build.gradle**: Dependencies and SDK versions
- **settings.gradle**: Module configuration
- **gradle.properties**: Build optimization

### SDK Versions
- compileSdk: 34
- targetSdk: 34
- minSdk: 24

### Dependencies
- androidx.appcompat:1.6.1
- material:1.11.0
- constraintlayout:2.1.4
- recyclerview:1.3.2
- cardview:1.0.0
- coordinatorlayout:1.2.0
- itextpdf:5.5.10

## Next Steps
1. Build the project: `./gradlew build`
2. Run on device: `./gradlew installDebug`
3. Test all features
4. Add unit tests
5. Add integration tests
6. Optimize performance
7. Add analytics
8. Add crash reporting
