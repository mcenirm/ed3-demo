package ed3.demo.cap;

public enum AlertScope {

  PUBLIC("Public"),
  RESTRICTED("Restricted"),
  PRIVATE("Private");
  private final String text;

  private AlertScope(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return this.text;
  }
}
