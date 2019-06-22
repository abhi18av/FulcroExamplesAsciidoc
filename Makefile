# gem install asciidoctor asciidoctor-diagram
# gem install coderay
docs/DevelopersGuide.html: DevelopersGuide.adoc
	asciidoctor -o DevelopersGuide.html -b html5 -r asciidoctor-diagram -r ./register-fulcro-examples.rb DevelopersGuide.adoc

book: docs/DevelopersGuide.html

