### Overview

The CLI tool is designed perform following changes:

**Inject new mapping rules for the following fields:**

1.   010$z Canceled LCCN.
2.   008 Date type.

**Inject new mapping rules with the following Subject types based on field number:**

1. 653 - Uncontrolled
2. 654 - Faceted Topical Terms
3. 656 - Occupation
4. 657 - Function
5. 658 - Curriculum Objective
6. 662 - Hierarchical Place Name
7. 688 - Type of Entity Unspecified

**Update default mapping for Subject types based on field**

1. 600 - Personal name
2. 610 - Corporate name
3. 611 - Meeting name
4. 630 - Uniform title
5. 647 - Named event
6. 648 - Chronological term
7. 650 - Topical term
8. 651 - Geographic name
9. 655 - Genre/Form

**The CLI tool/script does not change existing mapping rules on the target host, but it only extends existing rules.**