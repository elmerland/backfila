import { Classes, H1, HTMLTable } from "@blueprintjs/core"
import { ErrorCalloutComponent } from "@misk/core"
import * as React from "react"
import { Link } from "react-router-dom"

export interface ITableProps {
  data: any
  url?: string
  tag?: string
}

export const ServicesListComponent = (props: ITableProps) => {
  const { data } = props
  if (data) {
    /**
     * Data is loaded and ready to be rendered
     */
    return (
      <div>
        <H1>Services</H1>
        <HTMLTable bordered={true} striped={true}>
          <tbody>
            {data.map((service: string) => (
              <tr>
                <td>
                  <Link to={`/app/services/${service}`}>{service}</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </HTMLTable>
      </div>
    )
  } else {
    /**
     * Have a nice failure mode while your data is loading or doesn't load
     */
    const FakeCell = <p className={Classes.SKELETON}>lorem ipsum 1234 5678</p>
    return (
      <div>
        <H1>Services</H1>
        <HTMLTable bordered={true} striped={true}>
          <tbody>
            <tr>
              <td>{FakeCell}</td>
            </tr>
            <tr>
              <td>{FakeCell}</td>
            </tr>
            <tr>
              <td>{FakeCell}</td>
            </tr>
          </tbody>
        </HTMLTable>
        <ErrorCalloutComponent error={data.error} />
      </div>
    )
  }
}

export default ServicesListComponent
