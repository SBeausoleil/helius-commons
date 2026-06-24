# Content Accessors

A **`ContentAccessor`** answers a single question for `BeanIntrospector`:

> *"Given this object, what are the inner values I should keep searching through?"*

The introspector never reads an object directly. Instead, for every object it visits, it asks a
`ContentAccessor` to return a `Collection<Content>` — where each [`Content`](#the-content-record) is
a `(value, holdingField)` pair. More than one value may be bound to the same field 
(this is defined by the actual accessor implementation that handled the visited object).
The introspector then recurses into each returned value.

## The `ContentAccessor` contract

```java
public interface ContentAccessor {
    boolean accepts(Class<?> current, @Nullable Field holdingField);

    Collection<Content> extract(Object current,
                                @Nullable Field holdingField,
                                IntrospectionContext<?> context,
                                IntrospectionSettings settings) throws ChainComponentException;
}
```

- **`accepts`** — can this accessor handle an object of the given class in the given field? When an accessor accepts a
  type, no other accessor is queried for it.
- **`extract`** — produce the inner values to keep searching. Extraction may fail with a
  `ChainComponentException`, which carries an `allowFallback` flag telling the chain whether another
  accessor may be tried for the same value.

## The `Content` record

```java
public record Content(Object value, Field holdingField) {}
```

| Component      | Meaning                                                                                     |
|----------------|---------------------------------------------------------------------------------------------|
| `value`        | An inner value to keep searching through.                                                   |
| `holdingField` | The field that held `value`. Used by the introspector to distinguish primitives from wrappers. |

`holdingField` may be **synthetic** — accessors that read elements which have no real declaring
field (e.g. array elements) supply a representative field so the introspector can still resolve the
true primitive vs wrapper type.

## Built-in accessors

| Accessor                                            | Handles                          | Notes                                                  |
|-----------------------------------------------------|----------------------------------|--------------------------------------------------------|
| [`FieldHandlesAccessor`](FieldHandlesAccessor.md)   | Any plain object / bean          | Reads all instance fields, including inherited private ones. The last-resort accessor. |
| [`ArrayAccessor`](ArrayAccessor.md)                 | Arrays (primitive & object)      | Yields each element.                                   |
| [`IterativeAccessor`](IterativeAccessor.md)         | `Iterable` (lists, sets, …)      | Yields each element; does **not** read the collection's own fields. |
| [`IterativeMapAccessor`](IterativeMapAccessor.md)   | `Map`                            | Yields each key **and** value; does **not** read the map's own fields. |

> All built-in accessors are registered through `AccessorsChain`, the default `ContentAccessor`.
> The way the chain selects an accessor for a given input is an implementation detail that is
> expected to change; rely on the `ContentAccessor` contract rather than on chain ordering.

