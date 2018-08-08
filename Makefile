all:
	sh gradlew build --console=plain
re: clean all

test:
	sh gradlew clean test --console=plain

clean:
	sh gradlew clean --console=plain

.PHONY: test clean re all
