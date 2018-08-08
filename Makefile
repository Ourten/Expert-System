all:
	sh gradlew build --console=plain
re: clean all

clean:
	sh gradlew clean --console=plain

.PHONY: clean re all
