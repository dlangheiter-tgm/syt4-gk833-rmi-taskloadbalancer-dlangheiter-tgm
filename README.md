# Distributed Computing "*RMI Task Loadbalancer*" 

## Aufgabenstellung
Die detaillierte [Aufgabenstellung](TASK.md) beschreibt die notwendigen Schritte zur Realisierung.

## Implementierung
Fürs saubere Schließen habe ich einfach eine `while`-Schleife verwendet und den Input von der CLI gelesen. Wenn der eingegebene Text `exit` entspricht tue ich den STUB `unbinden` und schließe dann das Programm.

Um den Fibonacci Task zu implementieren habe ich einfach eine Neue klasse erstellt die Task implementiert. Diese hat beim erstellen einen Übergabe Parameter. Welche Fibonacci-Zahl ermittelt werden soll. Dann habe ich den Code vom Wiki in die Methode `fibonacci` gegeben und diese von `execute` Aufgerufen.

Um zu testen ob das funktioniert habe ich dann in `ComputePi` aufgerufen.

```java
Fibonacci taskFib = new Fibonacci(9999);
Integer fib = comp.executeTask(taskFib);
System.out.println(fib);
```

### Loadbalancer

Dafür habe ich als erstes ein neues Interface erstellt, damit ich von den `ComputeServer`n `register` und `unregister` aufrufen kann.

```java
public interface Loadbalancer extends Remote {
    void register(Compute c) throws RemoteException;
    void unregister(Compute c) throws RemoteException;
}
```

Danach habe ich `ComputeEngine` um dieses Interface erweitert und die Methoden implementiert. Dafür habe ich eine Liste erstellt von `Compute`s um sie an diese zu verteilen. Weiters habe ich die `executeTask` Methode so umgeschrieben das sie immer den nächsten `Compute` verwendet und bei diesem `executeTask` ausführt.

Der `ComputeServer` ist eigentlich fast das Selbe wie der `ComputeEngine` bevor wir ihn erweitert haben. Er holt sich die `registry`, exportiert `compute` und registriert den `computeStub` dann beim `loadbalancer`. Beim sauberen Schließen tut er beim Loadbalancer noch  `unregister` aufrufen.

## Quellen

* [Fibonacci Code Wiki](https://en.wikibooks.org/wiki/Algorithm_Implementation/Mathematics/Fibonacci_Number_Program#Java)