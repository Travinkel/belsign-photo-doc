BelSign Application - UI Style Guide

1. Purpose of the UI Design

BelSign is a photo documentation system used by workers in production environments, sometimes wearing gloves. Therefore, the UI must:

Be easy to use with large click targets

Be visually clear with high contrast

Minimize mistakes from misreadings

Be readable even from a distance or with limited dexterity

2. General Style Principles

Simplicity First: Only show necessary information. Hide optional details.

Large Interactive Elements: Buttons, links, and clickable areas must be large (at least 48x48px according to usability standards).

High Contrast Text: Dark text on light backgrounds (or vice versa). Avoid light grey on white.

Font Size: Minimum 16pt for body text, 20–24pt for headings.

Readable Fonts: Sans-serif fonts like Segoe UI, Roboto, Arial, Helvetica.

Touch-Friendly Layout: Minimum spacing between buttons and clickable elements (10–20px padding around elements).

3. Brand Colours

Primary Colours

Name

HEX

RGB

CMYK

Usage

Belman Design Light Blue

#7fa8c5

(127, 168, 197)

(C55, M25, Y15, K0)

Icons, Call-to-actions, Links, Highlighted messages

Belman Blue

#004b88

(0, 75, 136)

(C100, M60, Y0, K30)

com.belman.data.bootstrap.Main brand symbol, Icons, Important actions/messages

Belman Flexibles India Green

#338d71

(51, 141, 113)

(C75, M15, Y60, K15)

Flexibles branding, Secondary CTAs

Belman Dark Grey

#333535

(51, 53, 53)

(C48, M36, Y36, K80)

Document back pages, Footers

Secondary Colours (Greys)

Name

HEX

RGB

CMYK

Usage

Belman 80 Grey

#575757

(87, 87, 87)

(C0, M0, Y0, K80)

Table headers, Legal footers

Belman 50 Grey

#9d9d9d

(157, 157, 157)

(C0, M0, Y0, K50)

Info boxes, SoMe icons, Hover states

Belman 30 Grey

#c6c6c6

(198, 198, 198)

(C0, M0, Y0, K30)

Info boxes, Tables

Belman 20 Grey

#dadada

(218, 218, 218)

(C0, M0, Y0, K20)

Info highlight boxes, Table body, Some CTA buttons

Belman 15 Grey

#e3e3e3

(227, 227, 227)

(C0, M0, Y0, K15)

Image backgrounds, Text box backgrounds

Belman 07 Grey

#f2f2f2

(242, 242, 242)

(C0, M0, Y0, K7)

Website background, Light box backgrounds

4. Button Styles

Primary Buttons: Solid Belman Blue background with white text.

Secondary Buttons: Light Blue border, white background, blue text.

Touch Areas: Buttons must be large and padded (min height 48px, prefer 60px).

Example Primary Button (CSS):

.button-primary {
-fx-background-color: #004b88;
-fx-text-fill: white;
-fx-font-size: 18pt;
-fx-padding: 12px 24px;
-fx-background-radius: 6px;
}

5. Layout and Navigation

Clear Navigation: Simple top bar or side navigation.

Step-by-Step Flows: Break tasks into easy steps.

Progress Indicators: Show progress clearly when uploading photos.

Big Buttons: For actions like "Take Photo", "Save", "Upload".

6. Error Prevention

Confirmation Dialogs before destructive actions (like deleting a photo).

Validation on forms and inputs.

Big, clear error messages in case of failure (red text, high contrast).

7. Accessibility

High color contrast (> 4.5:1) for text.

Clear, non-ambiguous icons.

Avoid small text and small touch targets.

Support keyboard navigation (optional but good for robustness).

8. Future Enhancements

Introduce Responsive Layouts if the system must support smartphones or tablets fully.

Offer Dark Mode for low-light environments.

Recommended Visual Style Summary

Feature

Style

Background

Very light grey (#f2f2f2)

Text

Dark grey or black

Buttons

Large, filled with brand colors

Fonts

Large sans-serif fonts

Navigation

Simple, minimal options shown at once

Document Version: 1.0  Created for: BelSign - Belman A/S Photodocumentation System  Date: [Today]

Key Design Motto

"Make it readable, make it tappable, make it impossible to get wrong."

