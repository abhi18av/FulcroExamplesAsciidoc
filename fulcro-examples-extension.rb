# A block macro that embeds a Fulcro example into the output document
#
# Usage
#
#   $example$ "example_name", "example_id", "example_source"]
#   $example$ "victory_name", "victory_id", "./src/victory_example.cljs"]
#

require 'asciidoctor/extensions' 

include Asciidoctor

class TeXPreprocessor < Extensions::Preprocessor

  # Map $...$ to stem:[...]
  exampleShortForm = /(^|\s|\()\$(.*?)\$($|\s|\)|,|\.)/
  exampleFullForm = '\1latexmath:[\2]\3'

  def process document, reader
    return reader if reader.eof?
    replacement_lines = reader.read_lines.map do |line|
      if(line.include? '$example$')
        parameters = line.split("$")[2]
        puts parameters.split(",")[0]
        puts parameters.split(",")[1]
        puts parameters.split(",")[2]
        (line.gsub exampleShortForm, exampleFullForm)
      else line
      end
    end
    reader.unshift_lines replacement_lines
    reader
  end

end

