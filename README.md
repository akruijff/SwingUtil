# Swingility

Swingility allows to access the components within the Swing tree from 
Java.

## Licence

This project is distributed under the [3-clause BSD license](LICENSE).

## Installation

Maven:
```
<dependency>
  <groupId>org.kruijff</groupId>
  <artifactId>swingility</artifactId>
  <version>0.1</version>
  <scope>test</scope>
</dependency>
```

## Usage

```
import org.kruijff.swing

SwingUtil util = new SwingUtil(1000); //1000 ms timeout
JLabel emailLabel = util.fetchChildNamed(frame, "emailLabel", JLabel.class);
JTextField emailField = util.fetchChildNamed(frame, "emailField", JTextField.class);
JTextArea message = util.fetchChildNamed(frame, "message", JTextArea.class);
```

For more examples, please see the [unit tests](src/test/java/org/kruijff).

## Contributing

Pull request are welcome. For major changes, please open an issue first 
to discuss what you would like to change.

Please write well written commit messages. The template .gitmessage.txt
is included at the root. You can set this with `git config commit.template .gitmessage.txt`

Please update tests as appropiate.

## Distribution

```
mvn clean deploy -P release
```
