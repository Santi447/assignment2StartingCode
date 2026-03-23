# Assignment 2: XML Parser using Custom Data Structures

**Course:** CPRG-304  
**Institution:** Southern Alberta Institute of Technology (SAIT)  
**Assignment:** A2 - Creating ADTs, Implementing DS and an XML Parser  

---

## Team

| Name | Responsibility |
|------|---------------|
| Santiago | `MyArrayList.java` |
| Asad | `MyDLL.java` + `MyDLLNode.java` |
| Kaley | `MyStack.java` |
| Dylan | `MyQueue.java` |

> All members contributed to integration and the XML Parser.

---

## Project Overview

This project builds a working XML parser from scratch -- no `java.util.*` allowed for the core data structures. We implemented our own array list, doubly linked list, stack, and queue, then used them as the foundation for a parser that validates whether an XML file is correctly structured.

The parser reads an XML file, checks for tag mismatches and structural errors, and prints all problematic lines to the console in the order they appear.

---

## Project Structure

```
A2/
├── src/
│   ├── utilities/
│   │   ├── StackADT.java          # Stack interface (Part 1)
│   │   ├── QueueADT.java          # Queue interface (Part 1)
│   │   ├── ListADT.java           # Provided - do not modify
│   │   ├── Iterator.java          # Provided - do not modify
│   │   ├── MyArrayList.java       # Array-backed list (Santiago)
│   │   ├── MyDLL.java             # Doubly linked list (Asad)
│   │   ├── MyDLLNode.java         # Node class for DLL (Asad)
│   │   ├── MyStack.java           # Stack using MyArrayList (Kaley)
│   │   └── MyQueue.java           # Queue using MyDLL (Dylan)
│   └── parser/
│       └── XMLParser.java         # XML parsing logic (group)
├── test/
│   └── (JUnit test files - do not modify)
├── res/
│   └── (sample XML test files)
└── README.md
```

---

## How to Run

### From the Command Line

Make sure you have Java 8 installed, then run:

```bash
java -jar Parser.jar <path-to-xml-file>
```

**Example:**

```bash
java -jar Parser.jar res/sample1.xml
```

If the XML is valid, the parser will say so. If there are errors, each problematic line will be printed in the order it was encountered.

### From Eclipse

1. Import the project into Eclipse (`File > Import > Existing Projects into Workspace`)
2. Right-click `XMLParser.java` and select `Run As > Java Application`
3. Add the XML file path as a program argument under `Run Configurations > Arguments`

---

## Data Structures

Each structure was built without using `java.util.*` collection classes.

### MyArrayList
- Underlying structure: resizable array
- Implements: `ListADT<E>`, `Iterable<E>`
- Handles: dynamic resizing via `Arrays.copyOf()`

### MyDLL (Doubly Linked List)
- Underlying structure: nodes with `prev` and `next` pointers
- Implements: `ListADT<E>`, `Iterable<E>`
- Companion class: `MyDLLNode<E>`

### MyStack
- Underlying structure: backed by `MyArrayList`
- Implements: `StackADT<E>`, `Iterator<E>`
- Behavior: LIFO (Last In, First Out)

### MyQueue
- Underlying structure: backed by `MyDLL`
- Implements: `QueueADT<E>`, `Iterator<E>`
- Behavior: FIFO (First In, First Out)

---

## XML Parsing Rules

The parser checks the following:

- Opening tags use the format `<tag>`, closing tags use `</tag>`
- Every closing tag must have a matching opening tag
- Self-closing tags (`<tag/>`) are valid and require no closing tag
- Tags are case-sensitive
- Tag pairs cannot intercross (e.g. `<b><i></b></i>` is invalid)
- Every document must have exactly one root tag
- Processing instructions (`<?xml ... ?>`) are ignored

---

## Restrictions

> These apply to `MyArrayList`, `MyDLL`, `MyStack`, and `MyQueue` only.

- **No `java.util.*` collection classes** -- violation results in a 50% deduction
- `Arrays.copyOf()` and `System.arraycopy()` are allowed in `MyArrayList`
- Standard Java exceptions (`NullPointerException`, `NoSuchElementException`) are allowed
- The XML parser component has no restrictions on library use

---

## Submission Checklist

### (Data Structures and XML Parser)
- [ ] `Parser.jar` -- runnable from the command line
- [ ] Complete Eclipse project folder (exported correctly)
- [ ] Completed `MarkingCriteria_Assignment2.docx` signed off by all members
- [ ] Zipped as `A2P2[GroupName].zip`


## Tools and Requirements

- Java 8
- Eclipse IDE
- JUnit 4 (for testing -- do not modify test files)

---

## Disclaimer

This README was generated with the assistance of [Claude](https://claude.ai), an AI assistant made by Anthropic. 