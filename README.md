# Autocompletion
Command-line autocompletion mockup coded in Kotlin

## Build instructions
- In IntelliJ, make sure that under Project Settings->Artifacts building a JAR "from modules with dependencies" in configured.
- Hit "Build Artifacts" in IntelliJ
- In a terminal: 
```console
$java -jar out/artifacts/Autocompletion_jar/Autocompletion.jar
```
## Structure
### Resources
- Resources, such as data files, go into /data. The folder /data is marked as "Resource Folder" in the project settings.

### Code
- Code goes into /src. The folder /src is marked as "Source Folder" in the project settings.
- `Interface.kt` contains the `main()` function and a the `ConsoleWrapper` class which wraps all console business.
- `Trie.kt` contains the `Trie` class which holds the dictionary in an tree structure and exposes an iterator over words matching a query.

## Algorithms
### Console
The console is set to "raw" mode, which allows to read single keys pressed, jumping around with the cursor, etc. Keys are read one by one. Some special keys, such as ENTER or DEL trigger special action. All other keys are added to a query string. hile the user types, matching words from the dictionary are printed below the query string.

The ENTER key terminates the program. DEL deletes the last character of the query string, if any exists.

### Trie data structure
The prefix tree structure is a tree with two types of nodes: Leaf nodes and intermediate nodes. Leafs are always words, intermediate nodes are prefixes, but can be words at the same time. 

Example: "ar" is a prefix of "arm", which is a prefix of "army". That is, "army" is a child node of "arm" which is a child node of "ar". But unlike "ar", "arm" is also a word. "ar" and "arm" are intermediate nodes, "army" is a leaf node.

The implementation uses a simple map Char->Node, which basically labels child nodes with the next character needed to reach them from their parent.

### Iterator over words matching a query
The Trie class implements `Iterator<String>`. 

Once a sub-tree root has been identified based on a query string, it is possible to get all words below one-by-one. In oder to avoid recursion, a stack data structure is used. It is implemented in the `NodeStack()` class and used in the `next()` method. The trick is to take (`pop()`) the top node from the stack, `push()` all its child nodes onto the stack, and if the top node is a word node, return the word. If it isn't, the operation is repeated with the next node on the stack. This will eventually find a word node. 

The next call to `next()` will continue at the top of the stack.