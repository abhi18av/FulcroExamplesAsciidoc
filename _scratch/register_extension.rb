(require_relative './extension.rb')

Asciidoctor::Extensions.register do
  preprocessor FrontMatterPreprocessor
end

#Asciidoctor.convert_file 'sample-with-front-matter.adoc', :safe => :safe

